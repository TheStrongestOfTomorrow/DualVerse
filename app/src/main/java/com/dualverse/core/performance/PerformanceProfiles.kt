package com.dualverse.core.performance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Performance Profiles for Beta
 * Allows users to optimize gaming performance
 */
enum class PerformanceLevel {
    POWER_SAVER,    // Low power, reduced performance
    BALANCED,       // Default balance
    HIGH_PERFORMANCE, // Maximum performance
    CUSTOM          // User-defined settings
}

data class PerformanceProfile(
    val id: String,
    val name: String,
    val level: PerformanceLevel,
    val cpuCores: Int,           // Number of CPU cores to use
    val ramAllocation: Int,      // MB of RAM for container
    val gpuPriority: Int,        // GPU priority level (0-10)
    val networkPriority: Int,    // Network QoS priority
    val backgroundApps: Boolean, // Allow background apps
    val fpsCap: Int,             // FPS cap (0 = uncapped)
    val resolution: Float        // Resolution scale (0.5 - 1.0)
) {
    companion object {
        val PRESET_POWER_SAVER = PerformanceProfile(
            id = "power_saver",
            name = "Power Saver",
            level = PerformanceLevel.POWER_SAVER,
            cpuCores = 2,
            ramAllocation = 1024,
            gpuPriority = 3,
            networkPriority = 3,
            backgroundApps = false,
            fpsCap = 30,
            resolution = 0.75f
        )

        val PRESET_BALANCED = PerformanceProfile(
            id = "balanced",
            name = "Balanced",
            level = PerformanceLevel.BALANCED,
            cpuCores = 4,
            ramAllocation = 2048,
            gpuPriority = 5,
            networkPriority = 5,
            backgroundApps = true,
            fpsCap = 60,
            resolution = 1.0f
        )

        val PRESET_HIGH_PERFORMANCE = PerformanceProfile(
            id = "high_perf",
            name = "High Performance",
            level = PerformanceLevel.HIGH_PERFORMANCE,
            cpuCores = 8,
            ramAllocation = 4096,
            gpuPriority = 10,
            networkPriority = 10,
            backgroundApps = true,
            fpsCap = 0, // Uncapped
            resolution = 1.0f
        )
    }
}

/**
 * Performance Manager for Beta
 * Manages performance profiles and system optimization
 */
class PerformanceManager {

    private val _currentProfile = MutableStateFlow(PerformanceProfile.PRESET_BALANCED)
    val currentProfile: StateFlow<PerformanceProfile> = _currentProfile.asStateFlow()

    private val _customProfiles = MutableStateFlow<List<PerformanceProfile>>(emptyList())
    val customProfiles: StateFlow<List<PerformanceProfile>> = _customProfiles.asStateFlow()

    private val _isOptimizing = MutableStateFlow(false)
    val isOptimizing: StateFlow<Boolean> = _isOptimizing.asStateFlow()

    val allProfiles: List<PerformanceProfile>
        get() = listOf(
            PerformanceProfile.PRESET_POWER_SAVER,
            PerformanceProfile.PRESET_BALANCED,
            PerformanceProfile.PRESET_HIGH_PERFORMANCE
        ) + _customProfiles.value

    fun setProfile(profile: PerformanceProfile) {
        _currentProfile.value = profile
        applyProfile(profile)
    }

    fun createCustomProfile(
        name: String,
        cpuCores: Int,
        ramAllocation: Int,
        gpuPriority: Int,
        fpsCap: Int,
        resolution: Float
    ): PerformanceProfile {
        val profile = PerformanceProfile(
            id = "custom_${System.currentTimeMillis()}",
            name = name,
            level = PerformanceLevel.CUSTOM,
            cpuCores = cpuCores,
            ramAllocation = ramAllocation,
            gpuPriority = gpuPriority,
            networkPriority = 5,
            backgroundApps = true,
            fpsCap = fpsCap,
            resolution = resolution
        )
        _customProfiles.value = _customProfiles.value + profile
        return profile
    }

    fun deleteCustomProfile(profileId: String) {
        _customProfiles.value = _customProfiles.value.filter { it.id != profileId }
    }

    private fun applyProfile(profile: PerformanceProfile) {
        // In a real implementation, this would adjust system settings
        println("Applying performance profile: ${profile.name}")
    }

    suspend fun optimizeForGame(gamePackage: String) {
        _isOptimizing.value = true
        try {
            when {
                gamePackage.contains("roblox") -> setProfile(PerformanceProfile.PRESET_BALANCED)
                gamePackage.contains("pubg") -> setProfile(PerformanceProfile.PRESET_HIGH_PERFORMANCE)
                gamePackage.contains("freefire") -> setProfile(PerformanceProfile.PRESET_BALANCED)
                else -> setProfile(PerformanceProfile.PRESET_BALANCED)
            }
        } finally {
            _isOptimizing.value = false
        }
    }

    fun getSystemInfo(): SystemInfo {
        return SystemInfo(
            totalCores = Runtime.getRuntime().availableProcessors(),
            totalMemoryMB = (Runtime.getRuntime().maxMemory() / 1024 / 1024).toInt(),
            recommendedProfile = if (Runtime.getRuntime().availableProcessors() >= 8) {
                PerformanceProfile.PRESET_HIGH_PERFORMANCE
            } else {
                PerformanceProfile.PRESET_BALANCED
            }
        )
    }
}

data class SystemInfo(
    val totalCores: Int,
    val totalMemoryMB: Int,
    val recommendedProfile: PerformanceProfile
)
