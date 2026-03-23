package com.dualverse.core.multiinstance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Multi-Instance Manager for DualVerse v1.1
 * Supports running 3+ game accounts simultaneously
 */
data class GameInstance(
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val accountName: String,
    val containerId: String,
    val status: InstanceStatus = InstanceStatus.STOPPED,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis(),
    val deviceProfile: VirtualDeviceProfile
)

enum class InstanceStatus {
    STARTING, RUNNING, PAUSED, STOPPED, ERROR
}

data class VirtualDeviceProfile(
    val imei: String,
    val androidId: String,
    val macAddress: String,
    val serialNumber: String,
    val deviceModel: String = "DualVerse Virtual Device",
    val androidVersion: String = "8.1.0"
)

data class InstanceLimits(
    val maxInstances: Int = 5,
    val maxMemoryPerInstance: Int = 512,
    val maxCpuPerInstance: Int = 25
)

class MultiInstanceManager(
    private val limits: InstanceLimits = InstanceLimits()
) {
    private val _instances = MutableStateFlow<List<GameInstance>>(emptyList())
    val instances: StateFlow<List<GameInstance>> = _instances.asStateFlow()

    private val _activeCount = MutableStateFlow(0)
    val activeCount: StateFlow<Int> = _activeCount.asStateFlow()

    private val _isAtLimit = MutableStateFlow(false)
    val isAtLimit: StateFlow<Boolean> = _isAtLimit.asStateFlow()

    fun createInstance(
        packageName: String,
        accountName: String,
        deviceProfile: VirtualDeviceProfile
    ): Result<GameInstance> {
        if (_instances.value.size >= limits.maxInstances) {
            return Result.failure(IllegalStateException("Maximum instance limit reached (${limits.maxInstances})"))
        }

        val instance = GameInstance(
            packageName = packageName,
            accountName = accountName,
            containerId = "container_${System.currentTimeMillis()}",
            deviceProfile = deviceProfile
        )

        _instances.value = _instances.value + instance
        updateLimits()

        return Result.success(instance)
    }

    fun startInstance(instanceId: String): Result<Unit> {
        val instance = _instances.value.find { it.id == instanceId }
            ?: return Result.failure(IllegalArgumentException("Instance not found"))

        if (instance.status == InstanceStatus.RUNNING) {
            return Result.success(Unit)
        }

        updateInstanceStatus(instanceId, InstanceStatus.STARTING)

        Thread {
            Thread.sleep(2000)
            updateInstanceStatus(instanceId, InstanceStatus.RUNNING)
        }.start()

        return Result.success(Unit)
    }

    fun stopInstance(instanceId: String): Result<Unit> {
        updateInstanceStatus(instanceId, InstanceStatus.STOPPED)
        return Result.success(Unit)
    }

    fun pauseInstance(instanceId: String): Result<Unit> {
        updateInstanceStatus(instanceId, InstanceStatus.PAUSED)
        return Result.success(Unit)
    }

    fun resumeInstance(instanceId: String): Result<Unit> {
        updateInstanceStatus(instanceId, InstanceStatus.RUNNING)
        return Result.success(Unit)
    }

    fun deleteInstance(instanceId: String): Result<Unit> {
        _instances.value = _instances.value.filter { it.id != instanceId }
        updateLimits()
        return Result.success(Unit)
    }

    fun getInstancesForGame(packageName: String): List<GameInstance> {
        return _instances.value.filter { it.packageName == packageName }
    }

    fun getRunningInstances(): List<GameInstance> {
        return _instances.value.filter { it.status == InstanceStatus.RUNNING }
    }

    fun switchToInstance(instanceId: String): Result<GameInstance> {
        val instance = _instances.value.find { it.id == instanceId }
            ?: return Result.failure(IllegalArgumentException("Instance not found"))

        _instances.value = _instances.value.map {
            if (it.id == instanceId) it.copy(lastActive = System.currentTimeMillis()) else it
        }

        return Result.success(instance)
    }

    fun generateDeviceProfile(): VirtualDeviceProfile {
        return VirtualDeviceProfile(
            imei = generateIMEI(),
            androidId = generateAndroidId(),
            macAddress = generateMacAddress(),
            serialNumber = generateSerial()
        )
    }

    private fun updateInstanceStatus(instanceId: String, status: InstanceStatus) {
        _instances.value = _instances.value.map {
            if (it.id == instanceId) it.copy(status = status) else it
        }
        _activeCount.value = _instances.value.count { it.status == InstanceStatus.RUNNING }
    }

    private fun updateLimits() {
        _isAtLimit.value = _instances.value.size >= limits.maxInstances
    }

    private fun generateIMEI(): String {
        val random = java.util.Random()
        val sb = StringBuilder()
        for (i in 0 until 15) {
            sb.append(random.nextInt(10))
        }
        return sb.toString()
    }

    private fun generateAndroidId(): String {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16)
    }

    private fun generateMacAddress(): String {
        val random = java.util.Random()
        return StringBuilder().apply {
            for (i in 0 until 6) {
                if (i > 0) append(":")
                append(String.format("%02X", random.nextInt(256)))
            }
        }.toString()
    }

    private fun generateSerial(): String {
        return "DV${System.currentTimeMillis().toString(16).uppercase()}"
    }
}
