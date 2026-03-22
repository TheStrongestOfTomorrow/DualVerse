package com.dualverse.core.graphics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Graphics Renderer Selector for DualVerse Beta
 * Allows users to choose between Vulkan, OpenGL, and OpenGL ES renderers
 * for optimal gaming performance
 */

enum class GraphicsApi {
    VULKAN,         // Modern API, best for newer devices
    OPENGL,         // Desktop OpenGL (for compatibility)
    OPENGL_ES,      // OpenGL ES (mobile standard)
    AUTO            // Auto-select based on device
}

enum class GlVersion(val version: String, val api: GraphicsApi) {
    GL_ES_2_0("OpenGL ES 2.0", GraphicsApi.OPENGL_ES),
    GL_ES_3_0("OpenGL ES 3.0", GraphicsApi.OPENGL_ES),
    GL_ES_3_1("OpenGL ES 3.1", GraphicsApi.OPENGL_ES),
    GL_ES_3_2("OpenGL ES 3.2", GraphicsApi.OPENGL_ES),
    VULKAN_1_0("Vulkan 1.0", GraphicsApi.VULKAN),
    VULKAN_1_1("Vulkan 1.1", GraphicsApi.VULKAN),
    VULKAN_1_2("Vulkan 1.2", GraphicsApi.VULKAN),
    VULKAN_1_3("Vulkan 1.3", GraphicsApi.VULKAN),
    OPENGL_4_5("OpenGL 4.5", GraphicsApi.OPENGL),
    OPENGL_4_6("OpenGL 4.6", GraphicsApi.OPENGL)
}

data class RendererConfig(
    val api: GraphicsApi,
    val version: GlVersion,
    val vsync: Boolean = true,
    val tripleBuffering: Boolean = true,
    val msaa: Int = 4,              // Anti-aliasing samples (0, 2, 4, 8)
    val anisotropicFiltering: Int = 16,  // 0, 2, 4, 8, 16
    val fpsTarget: Int = 60,
    val enableGpuScheduling: Boolean = true,
    val preferFrontBuffer: Boolean = false,
    val enableAsyncCompute: Boolean = true,  // Vulkan only
    val enableRayTracing: Boolean = false     // Vulkan only, if supported
)

data class RendererCapabilities(
    val supportsVulkan: Boolean,
    val vulkanVersion: String?,
    val supportsOpenGlEs: Boolean,
    val openGlEsVersion: String?,
    val supportsOpenGl: Boolean,
    val openGlVersion: String?,
    val maxTextureSize: Int,
    val maxMsaaSamples: Int,
    val supportsRayTracing: Boolean,
    val gpuName: String,
    val gpuVendor: String,
    val totalVramMB: Int
)

data class RendererPreset(
    val id: String,
    val name: String,
    val description: String,
    val config: RendererConfig
) {
    companion object {
        val PERFORMANCE = RendererPreset(
            id = "performance",
            name = "Performance",
            description = "Maximum FPS, reduced visual quality",
            config = RendererConfig(
                api = GraphicsApi.VULKAN,
                version = GlVersion.VULKAN_1_1,
                vsync = false,
                tripleBuffering = false,
                msaa = 0,
                anisotropicFiltering = 0,
                fpsTarget = 120,
                enableGpuScheduling = true,
                enableAsyncCompute = true
            )
        )

        val BALANCED = RendererPreset(
            id = "balanced",
            name = "Balanced",
            description = "Good visuals and performance",
            config = RendererConfig(
                api = GraphicsApi.VULKAN,
                version = GlVersion.VULKAN_1_1,
                vsync = true,
                tripleBuffering = true,
                msaa = 4,
                anisotropicFiltering = 8,
                fpsTarget = 60,
                enableGpuScheduling = true,
                enableAsyncCompute = true
            )
        )

        val QUALITY = RendererPreset(
            id = "quality",
            name = "Quality",
            description = "Best visuals, may reduce FPS",
            config = RendererConfig(
                api = GraphicsApi.VULKAN,
                version = GlVersion.VULKAN_1_2,
                vsync = true,
                tripleBuffering = true,
                msaa = 8,
                anisotropicFiltering = 16,
                fpsTarget = 60,
                enableGpuScheduling = true,
                enableAsyncCompute = true,
                enableRayTracing = true
            )
        )

        val COMPATIBILITY = RendererPreset(
            id = "compatibility",
            name = "Compatibility",
            description = "OpenGL ES for older devices",
            config = RendererConfig(
                api = GraphicsApi.OPENGL_ES,
                version = GlVersion.GL_ES_3_0,
                vsync = true,
                tripleBuffering = true,
                msaa = 2,
                anisotropicFiltering = 4,
                fpsTarget = 60,
                enableGpuScheduling = false,
                enableAsyncCompute = false
            )
        )

        val PRESETS = listOf(PERFORMANCE, BALANCED, QUALITY, COMPATIBILITY)
    }
}

class GraphicsRendererSelector {

    private val _currentConfig = MutableStateFlow<RendererConfig?>(null)
    val currentConfig: StateFlow<RendererConfig?> = _currentConfig.asStateFlow()

    private val _capabilities = MutableStateFlow<RendererCapabilities?>(null)
    val capabilities: StateFlow<RendererCapabilities?> = _capabilities.asStateFlow()

    private val _availableApis = MutableStateFlow<List<GraphicsApi>>(emptyList())
    val availableApis: StateFlow<List<GraphicsApi>> = _availableApis.asStateFlow()

    private val _selectedApi = MutableStateFlow<GraphicsApi>(GraphicsApi.AUTO)
    val selectedApi: StateFlow<GraphicsApi> = _selectedApi.asStateFlow()

    /**
     * Initialize and detect device capabilities
     */
    fun initialize() {
        val caps = detectCapabilities()
        _capabilities.value = caps

        // Build list of available APIs
        val apis = mutableListOf<GraphicsApi>()
        if (caps.supportsVulkan) apis.add(GraphicsApi.VULKAN)
        if (caps.supportsOpenGlEs) apis.add(GraphicsApi.OPENGL_ES)
        if (caps.supportsOpenGl) apis.add(GraphicsApi.OPENGL)
        apis.add(GraphicsApi.AUTO)
        _availableApis.value = apis

        // Auto-select best renderer
        val bestConfig = getBestRendererConfig(caps)
        _currentConfig.value = bestConfig
        _selectedApi.value = bestConfig.api
    }

    /**
     * Set graphics API
     */
    fun setGraphicsApi(api: GraphicsApi): Result<RendererConfig> {
        val caps = _capabilities.value ?: return Result.failure(IllegalStateException("Not initialized"))

        if (api != GraphicsApi.AUTO && api !in _availableApis.value) {
            return Result.failure(IllegalArgumentException("API $api not supported on this device"))
        }

        val config = when (api) {
            GraphicsApi.VULKAN -> RendererConfig(
                api = GraphicsApi.VULKAN,
                version = if (caps.vulkanVersion?.contains("1.3") == true) GlVersion.VULKAN_1_3 else GlVersion.VULKAN_1_1,
                vsync = true,
                tripleBuffering = true,
                msaa = 4,
                anisotropicFiltering = 8,
                fpsTarget = 60,
                enableAsyncCompute = caps.supportsVulkan,
                enableRayTracing = caps.supportsRayTracing
            )
            GraphicsApi.OPENGL_ES -> RendererConfig(
                api = GraphicsApi.OPENGL_ES,
                version = GlVersion.values().filter { it.api == GraphicsApi.OPENGL_ES }
                    .sortedByDescending { it.version }
                    .find { caps.openGlEsVersion?.contains(it.version.substringAfter(" ")) == true }
                    ?: GlVersion.GL_ES_3_0,
                vsync = true,
                tripleBuffering = true,
                msaa = minOf(4, caps.maxMsaaSamples),
                anisotropicFiltering = 8,
                fpsTarget = 60
            )
            GraphicsApi.OPENGL -> RendererConfig(
                api = GraphicsApi.OPENGL,
                version = GlVersion.OPENGL_4_5,
                vsync = true,
                tripleBuffering = true,
                msaa = 4,
                anisotropicFiltering = 16,
                fpsTarget = 60
            )
            GraphicsApi.AUTO -> getBestRendererConfig(caps)
        }

        _currentConfig.value = config
        _selectedApi.value = api
        return Result.success(config)
    }

    /**
     * Apply a preset configuration
     */
    fun applyPreset(preset: RendererPreset): Result<RendererConfig> {
        val caps = _capabilities.value ?: return Result.failure(IllegalStateException("Not initialized"))

        // Check if preset API is available
        if (preset.config.api != GraphicsApi.AUTO && preset.config.api !in _availableApis.value) {
            return Result.failure(IllegalArgumentException("Preset requires ${preset.config.api} which is not available"))
        }

        // Adjust MSAA to device limits
        val adjustedConfig = preset.config.copy(
            msaa = minOf(preset.config.msaa, caps.maxMsaaSamples).let {
                if (it == 1) 0 else it
            }
        )

        _currentConfig.value = adjustedConfig
        _selectedApi.value = adjustedConfig.api
        return Result.success(adjustedConfig)
    }

    /**
     * Set custom configuration
     */
    fun setCustomConfig(config: RendererConfig): Result<RendererConfig> {
        val caps = _capabilities.value ?: return Result.failure(IllegalStateException("Not initialized"))

        // Validate configuration
        if (config.msaa > caps.maxMsaaSamples) {
            return Result.failure(IllegalArgumentException("MSAA ${config.msaa}x not supported (max: ${caps.maxMsaaSamples}x)"))
        }

        _currentConfig.value = config
        _selectedApi.value = config.api
        return Result.success(config)
    }

    /**
     * Get recommended API for a specific game
     */
    fun getRecommendedApi(gamePackage: String): GraphicsApi {
        return when {
            // Games that prefer Vulkan
            gamePackage.contains("pubg", ignoreCase = true) ||
            gamePackage.contains("cod", ignoreCase = true) ||
            gamePackage.contains("callofduty", ignoreCase = true) -> {
                if (GraphicsApi.VULKAN in _availableApis.value) GraphicsApi.VULKAN else GraphicsApi.AUTO
            }

            // Games that work well with OpenGL ES
            gamePackage.contains("roblox", ignoreCase = true) ||
            gamePackage.contains("freefire", ignoreCase = true) ||
            gamePackage.contains("mobilelegends", ignoreCase = true) -> {
                GraphicsApi.OPENGL_ES
            }

            // Genshin Impact prefers Vulkan on capable devices
            gamePackage.contains("genshin", ignoreCase = true) -> {
                if (GraphicsApi.VULKAN in _availableApis.value) GraphicsApi.VULKAN else GraphicsApi.OPENGL_ES
            }

            // Default to auto
            else -> GraphicsApi.AUTO
        }
    }

    /**
     * Get game-specific preset
     */
    fun getGamePreset(gamePackage: String): RendererPreset {
        return when {
            gamePackage.contains("pubg", ignoreCase = true) -> RendererPreset.PERFORMANCE
            gamePackage.contains("genshin", ignoreCase = true) -> RendererPreset.QUALITY
            gamePackage.contains("freefire", ignoreCase = true) -> RendererPreset.BALANCED
            gamePackage.contains("roblox", ignoreCase = true) -> RendererPreset.COMPATIBILITY
            else -> RendererPreset.BALANCED
        }
    }

    private fun detectCapabilities(): RendererCapabilities {
        // In a real implementation, this would query the actual device capabilities
        // For now, return simulated capabilities for a typical high-end Android device
        return RendererCapabilities(
            supportsVulkan = true,
            vulkanVersion = "1.1.128",
            supportsOpenGlEs = true,
            openGlEsVersion = "3.2",
            supportsOpenGl = false, // Not typically available on Android
            openGlVersion = null,
            maxTextureSize = 8192,
            maxMsaaSamples = 8,
            supportsRayTracing = false,
            gpuName = "Adreno 650",
            gpuVendor = "Qualcomm",
            totalVramMB = 512
        )
    }

    private fun getBestRendererConfig(caps: RendererCapabilities): RendererConfig {
        return when {
            caps.supportsVulkan -> RendererConfig(
                api = GraphicsApi.VULKAN,
                version = GlVersion.VULKAN_1_1,
                vsync = true,
                tripleBuffering = true,
                msaa = 4,
                anisotropicFiltering = 8,
                fpsTarget = 60,
                enableAsyncCompute = true
            )
            caps.supportsOpenGlEs -> RendererConfig(
                api = GraphicsApi.OPENGL_ES,
                version = GlVersion.GL_ES_3_2,
                vsync = true,
                tripleBuffering = true,
                msaa = 4,
                anisotropicFiltering = 8,
                fpsTarget = 60
            )
            else -> RendererConfig(
                api = GraphicsApi.OPENGL_ES,
                version = GlVersion.GL_ES_2_0,
                vsync = true,
                tripleBuffering = false,
                msaa = 0,
                anisotropicFiltering = 0,
                fpsTarget = 30
            )
        }
    }
}
