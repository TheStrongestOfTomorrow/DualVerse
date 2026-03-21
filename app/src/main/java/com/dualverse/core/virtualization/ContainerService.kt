package com.dualverse.core.virtualization

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages container creation, isolation, and lifecycle operations.
 * Implements lightweight containerization for the virtualized Android environment.
 */
class ContainerService(private val context: Context) {

    /**
     * Configuration for creating a new container.
     */
    data class ContainerConfig(
        val containerId: String,
        val rootfs: File,
        val memoryLimit: Long,
        val cpuShares: Int,
        val networkMode: NetworkNamespace.NetworkMode,
        val deviceMappings: List<DeviceMapping>
    )

    /**
     * Device mapping for hardware passthrough.
     */
    data class DeviceMapping(
        val hostPath: String,
        val containerPath: String,
        val permissions: String
    )

    /**
     * Represents a running container.
     */
    data class Container(
        val id: String,
        val pid: Int,
        val rootfs: File,
        val state: State,
        val created: Long
    ) {
        enum class State {
            Created, Running, Paused, Stopped, Error
        }
    }

    // Active containers
    private val containers = ConcurrentHashMap<String, Container>()

    // Native interface
    private external fun nativeCreateContainer(configJson: String): Int
    private external fun nativeStartContainer(containerId: String): Boolean
    private external fun nativeStopContainer(containerId: String): Boolean
    private external fun nativePauseContainer(containerId: String): Boolean
    private external fun nativeResumeContainer(containerId: String): Boolean
    private external fun nativeDestroyContainer(containerId: String): Boolean

    companion object {
        init {
            System.loadLibrary("dualverse-native")
        }
    }

    /**
     * Creates a new container with the specified configuration.
     */
    fun createContainer(config: ContainerConfig): Result<Container> {
        return try {
            Timber.i("Creating container: ${config.containerId}")

            // Prepare the container rootfs
            prepareRootfs(config.rootfs, config.containerId)

            // Create container via native code
            val configJson = buildContainerConfigJson(config)
            val pid = nativeCreateContainer(configJson)

            if (pid <= 0) {
                return Result.failure(Exception("Failed to create container: native error"))
            }

            val container = Container(
                id = config.containerId,
                pid = pid,
                rootfs = config.rootfs,
                state = Container.State.Created,
                created = System.currentTimeMillis()
            )

            containers[config.containerId] = container
            Timber.i("Container created successfully: ${config.containerId} (PID: $pid)")
            Result.success(container)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create container: ${config.containerId}")
            Result.failure(e)
        }
    }

    /**
     * Starts a container by its ID.
     */
    fun startContainer(containerId: String): Result<Unit> {
        return try {
            val container = containers[containerId]
                ?: return Result.failure(IllegalArgumentException("Container not found: $containerId"))

            Timber.i("Starting container: $containerId")
            
            val success = nativeStartContainer(containerId)
            if (!success) {
                return Result.failure(Exception("Failed to start container"))
            }

            containers[containerId] = container.copy(state = Container.State.Running)
            Timber.i("Container started: $containerId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start container: $containerId")
            Result.failure(e)
        }
    }

    /**
     * Stops a container by its ID.
     */
    fun stopContainer(containerId: String): Result<Unit> {
        return try {
            val container = containers[containerId]
                ?: return Result.failure(IllegalArgumentException("Container not found: $containerId"))

            Timber.i("Stopping container: $containerId")
            
            val success = nativeStopContainer(containerId)
            if (!success) {
                return Result.failure(Exception("Failed to stop container"))
            }

            containers[containerId] = container.copy(state = Container.State.Stopped)
            Timber.i("Container stopped: $containerId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop container: $containerId")
            Result.failure(e)
        }
    }

    /**
     * Destroys a container and releases its resources.
     */
    fun destroyContainer(containerId: String): Result<Unit> {
        return try {
            Timber.i("Destroying container: $containerId")
            
            val success = nativeDestroyContainer(containerId)
            if (!success) {
                return Result.failure(Exception("Failed to destroy container"))
            }

            containers.remove(containerId)
            Timber.i("Container destroyed: $containerId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to destroy container: $containerId")
            Result.failure(e)
        }
    }

    /**
     * Pauses a running container.
     */
    fun pauseContainer(containerId: String): Result<Unit> {
        return try {
            val container = containers[containerId]
                ?: return Result.failure(IllegalArgumentException("Container not found: $containerId"))

            val success = nativePauseContainer(containerId)
            if (!success) {
                return Result.failure(Exception("Failed to pause container"))
            }

            containers[containerId] = container.copy(state = Container.State.Paused)
            Timber.i("Container paused: $containerId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to pause container: $containerId")
            Result.failure(e)
        }
    }

    /**
     * Resumes a paused container.
     */
    fun resumeContainer(containerId: String): Result<Unit> {
        return try {
            val container = containers[containerId]
                ?: return Result.failure(IllegalArgumentException("Container not found: $containerId"))

            val success = nativeResumeContainer(containerId)
            if (!success) {
                return Result.failure(Exception("Failed to resume container"))
            }

            containers[containerId] = container.copy(state = Container.State.Running)
            Timber.i("Container resumed: $containerId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to resume container: $containerId")
            Result.failure(e)
        }
    }

    /**
     * Stops all active containers.
     */
    fun stopAllContainers() {
        containers.keys.toList().forEach { containerId ->
            stopContainer(containerId)
        }
    }

    /**
     * Pauses all running containers.
     */
    fun pauseAllContainers() {
        containers.values
            .filter { it.state == Container.State.Running }
            .forEach { container ->
                pauseContainer(container.id)
            }
    }

    /**
     * Resumes all paused containers.
     */
    fun resumeAllContainers() {
        containers.values
            .filter { it.state == Container.State.Paused }
            .forEach { container ->
                resumeContainer(container.id)
            }
    }

    /**
     * Gets the ID of the currently active container.
     */
    fun getActiveContainerId(): String? {
        return containers.values
            .firstOrNull { it.state == Container.State.Running }
            ?.id
    }

    /**
     * Gets all active containers.
     */
    fun getContainers(): List<Container> = containers.values.toList()

    /**
     * Gets a container by ID.
     */
    fun getContainer(containerId: String): Container? = containers[containerId]

    // Private helper methods

    private fun prepareRootfs(rootfs: File, containerId: String) {
        val containerDir = File(context.filesDir, "containers/$containerId")
        if (!containerDir.exists()) {
            containerDir.mkdirs()
        }

        // Create overlay directories for writable layers
        val upperDir = File(containerDir, "upper")
        val workDir = File(containerDir, "work")
        val mergedDir = File(containerDir, "merged")

        upperDir.mkdirs()
        workDir.mkdirs()
        mergedDir.mkdirs()

        Timber.d("Prepared rootfs for container: $containerId")
    }

    private fun buildContainerConfigJson(config: ContainerConfig): String {
        val deviceMappingsJson = config.deviceMappings.joinToString(",") { mapping ->
            """{"host":"${mapping.hostPath}","container":"${mapping.containerPath}","perms":"${mapping.permissions}"}"""
        }

        return """{
            "id":"${config.containerId}",
            "rootfs":"${config.rootfs.absolutePath}",
            "memory_mb":${config.memoryLimit},
            "cpu_shares":${config.cpuShares},
            "network_mode":"${config.networkMode.name}",
            "devices":[$deviceMappingsJson]
        }""".replace("\n", "").replace(" ", "")
    }
}
