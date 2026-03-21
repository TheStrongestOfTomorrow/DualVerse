package com.dualverse.core.virtualization

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.UUID

/**
 * Manages the lifecycle and resources of virtual Android instances.
 * This is the core component that handles container creation, resource allocation,
 * and coordination between the host system and virtualized environments.
 */
class VirtualMachineManager(
    private val context: Context,
    private val config: VirtualMachineConfig
) {
    /**
     * Represents the current state of a virtual machine instance.
     */
    sealed class State {
        /** VM is not running */
        object Idle : State()
        /** VM is in the process of starting */
        object Starting : State()
        /** VM is running and ready */
        object Running : State()
        /** VM is paused */
        object Paused : State()
        /** VM is in the process of stopping */
        object Stopping : State()
        /** VM has stopped */
        object Stopped : State()
        /** VM encountered an error */
        data class Error(val message: String) : State()
    }

    /**
     * Configuration for resource allocation.
     */
    data class ResourceConfig(
        val memoryMB: Long = 2048,
        val cpuShares: Int = 512,
        val storageMB: Long = 1024,
        val gpuAcceleration: Boolean = true
    )

    // Core components
    private val containerService: ContainerService = ContainerService(context)
    private val memoryBridge: MemoryBridge = MemoryBridge()
    private val storageIsolator: StorageIsolator = StorageIsolator(context)
    private val networkNamespace: NetworkNamespace = NetworkNamespace()

    // State management
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    // Active instances
    private val _instances = MutableStateFlow<Map<String, VirtualInstance>>(emptyMap())
    val instances: StateFlow<Map<String, VirtualInstance>> = _instances.asStateFlow()

    // Current resource allocation
    private var currentAllocation: AllocationResult? = null

    /**
     * Starts the virtual machine with the given configuration.
     */
    suspend fun startVirtualMachine(): Result<VirtualMachine> = withContext(Dispatchers.IO) {
        try {
            _state.value = State.Starting
            Timber.i("Starting virtual machine...")

            val requirementsCheck = checkSystemRequirements()
            if (requirementsCheck.isFailure) {
                _state.value = State.Error(requirementsCheck.exceptionOrNull()?.message ?: "System requirements not met")
                return@withContext Result.failure(requirementsCheck.exceptionOrNull()!!)
            }

            val allocation = allocateResources(config.resourceConfig)
            if (allocation.isFailure) {
                _state.value = State.Error(allocation.exceptionOrNull()?.message ?: "Resource allocation failed")
                return@withContext Result.failure(allocation.exceptionOrNull()!!)
            }
            currentAllocation = allocation.getOrNull()

            val containerResult = containerService.createContainer(
                ContainerService.ContainerConfig(
                    containerId = generateContainerId(),
                    rootfs = getRomFile(),
                    memoryLimit = config.resourceConfig.memoryMB,
                    cpuShares = config.resourceConfig.cpuShares,
                    networkMode = NetworkNamespace.NetworkMode.ISOLATED,
                    deviceMappings = getDeviceMappings()
                )
            )

            if (containerResult.isFailure) {
                _state.value = State.Error(containerResult.exceptionOrNull()?.message ?: "Container creation failed")
                return@withContext Result.failure(containerResult.exceptionOrNull()!!)
            }

            val container = containerResult.getOrNull()
            containerService.startContainer(container!!.id)
            networkNamespace.createNamespace(container.id)

            _state.value = State.Running
            Timber.i("Virtual machine started successfully")

            Result.success(
                VirtualMachine(
                    id = container.id,
                    container = container,
                    resourceConfig = config.resourceConfig
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to start virtual machine")
            _state.value = State.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * Stops the virtual machine and releases all resources.
     */
    suspend fun stopVirtualMachine(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _state.value = State.Stopping
            Timber.i("Stopping virtual machine...")

            _instances.value.keys.forEach { instanceId ->
                destroyInstance(instanceId)
            }
            containerService.stopAllContainers()
            releaseResources()
            networkNamespace.cleanupAll()

            _state.value = State.Stopped
            Timber.i("Virtual machine stopped successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop virtual machine")
            _state.value = State.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * Pauses the virtual machine execution.
     */
    suspend fun pauseVirtualMachine(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (_state.value != State.Running) {
                return@withContext Result.failure(IllegalStateException("VM is not running"))
            }
            containerService.pauseAllContainers()
            _state.value = State.Paused
            Timber.i("Virtual machine paused")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to pause virtual machine")
            Result.failure(e)
        }
    }

    /**
     * Resumes a paused virtual machine.
     */
    suspend fun resumeVirtualMachine(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (_state.value != State.Paused) {
                return@withContext Result.failure(IllegalStateException("VM is not paused"))
            }
            containerService.resumeAllContainers()
            _state.value = State.Running
            Timber.i("Virtual machine resumed")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to resume virtual machine")
            Result.failure(e)
        }
    }

    /**
     * Allocates system resources for the virtual machine.
     */
    fun allocateResources(config: ResourceConfig): Result<AllocationResult> {
        return try {
            val availableMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024)
            val requiredMemory = config.memoryMB

            if (requiredMemory > availableMemory * 0.7) {
                return Result.failure(
                    IllegalStateException("Insufficient memory. Required: ${requiredMemory}MB, Available: ${availableMemory}MB")
                )
            }

            val result = AllocationResult(
                allocatedMemoryMB = config.memoryMB,
                allocatedCpuShares = config.cpuShares,
                allocatedStorageMB = config.storageMB,
                gpuEnabled = config.gpuAcceleration
            )

            Timber.d("Resources allocated: $result")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Failed to allocate resources")
            Result.failure(e)
        }
    }

    /**
     * Releases all allocated resources.
     */
    fun releaseResources() {
        currentAllocation = null
        memoryBridge.cleanup()
        Timber.d("Resources released")
    }

    /**
     * Creates a new virtual instance for running an app.
     */
    suspend fun createInstance(appPackage: String): VirtualInstance = withContext(Dispatchers.IO) {
        val instanceId = generateInstanceId()
        storageIsolator.createContainerStorage(instanceId)
        
        val hostAppExists = isAppInstalled(appPackage)
        if (hostAppExists) {
            storageIsolator.copyAppData(appPackage, instanceId)
        }

        val instance = VirtualInstance(
            id = instanceId,
            appPackage = appPackage,
            containerId = containerService.getActiveContainerId(),
            state = VirtualInstance.State.Ready
        )

        _instances.value = _instances.value + (instanceId to instance)
        Timber.i("Created instance: $instanceId for app: $appPackage")
        instance
    }

    /**
     * Destroys a virtual instance and releases its resources.
     */
    suspend fun destroyInstance(instanceId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val instance = _instances.value[instanceId]
                ?: return@withContext Result.failure(IllegalArgumentException("Instance not found: $instanceId"))

            storageIsolator.clearContainerData(instanceId)
            _instances.value = _instances.value - instanceId
            
            Timber.i("Destroyed instance: $instanceId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to destroy instance: $instanceId")
            Result.failure(e)
        }
    }

    private fun checkSystemRequirements(): Result<Unit> {
        val abi = System.getProperty("os.arch") ?: ""
        if (!abi.contains("arm", ignoreCase = true)) {
            return Result.failure(IllegalStateException("Only ARM architecture is supported"))
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            return Result.failure(IllegalStateException("Android 7.0 or higher is required"))
        }

        val dataDir = context.filesDir
        val freeSpace = dataDir.freeSpace / (1024 * 1024)
        if (freeSpace < 500) {
            return Result.failure(IllegalStateException("Insufficient storage. At least 500MB required"))
        }

        return Result.success(Unit)
    }

    private fun getRomFile(): File {
        val romFile = File(context.filesDir, "rom/system.img")
        if (!romFile.exists()) {
            throw IllegalStateException("ROM file not found. Please reinstall the app.")
        }
        return romFile
    }

    private fun getDeviceMappings(): List<ContainerService.DeviceMapping> {
        return listOf(
            ContainerService.DeviceMapping(
                hostPath = "/dev/mali0",
                containerPath = "/dev/mali0",
                permissions = "rwm"
            ),
            ContainerService.DeviceMapping(
                hostPath = "/dev/dri/renderD128",
                containerPath = "/dev/dri/renderD128",
                permissions = "rwm"
            )
        )
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun generateContainerId(): String = "dv_${UUID.randomUUID().toString().take(8)}"
    private fun generateInstanceId(): String = "inst_${UUID.randomUUID().toString().take(8)}"

    data class AllocationResult(
        val allocatedMemoryMB: Long,
        val allocatedCpuShares: Int,
        val allocatedStorageMB: Long,
        val gpuEnabled: Boolean
    )
}

data class VirtualMachineConfig(
    val resourceConfig: VirtualMachineManager.ResourceConfig = VirtualMachineManager.ResourceConfig(),
    val enableDebugging: Boolean = false
)

data class VirtualMachine(
    val id: String,
    val container: ContainerService.Container,
    val resourceConfig: VirtualMachineManager.ResourceConfig
)

data class VirtualInstance(
    val id: String,
    val appPackage: String,
    val containerId: String?,
    val state: State
) {
    enum class State {
        Creating, Ready, Starting, Running, Paused, Stopping, Stopped, Error
    }
}
