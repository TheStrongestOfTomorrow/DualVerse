package com.dualverse.core.romselector

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Custom ROM Selection for DualVerse v1.1
 * Allows users to choose different Android ROMs
 */
data class RomInfo(
    val id: String,
    val name: String,
    val version: String,
    val androidVersion: String,
    val size: Long,
    val checksum: String,
    val downloadUrl: String? = null,
    val isDownloaded: Boolean = false,
    val isDefault: Boolean = false,
    val features: List<String> = emptyList(),
    val minApi: Int = 26,
    val targetApi: Int = 28
)

data class RomVariant(
    val id: String,
    val displayName: String,
    val description: String,
    val sizeMB: Int,
    val recommended: Boolean = false
) {
    companion object {
        val AVAILABLE_VARIANTS = listOf(
            RomVariant(
                id = "android81_light",
                displayName = "Android 8.1 Light",
                description = "Minimal system, fastest boot, best for low-end devices",
                sizeMB = 193,
                recommended = true
            ),
            RomVariant(
                id = "android81_full",
                displayName = "Android 8.1 Full",
                description = "Complete system with Google Services",
                sizeMB = 350,
                recommended = false
            ),
            RomVariant(
                id = "android10_light",
                displayName = "Android 10 Light",
                description = "Modern Android with scoped storage",
                sizeMB = 280,
                recommended = false
            ),
            RomVariant(
                id = "android11_gaming",
                displayName = "Android 11 Gaming",
                description = "Optimized for gaming performance",
                sizeMB = 320,
                recommended = false
            )
        )
    }
}

enum class RomStatus {
    NOT_DOWNLOADED, DOWNLOADING, EXTRACTING, READY, ERROR
}

data class RomDownloadProgress(
    val status: RomStatus,
    val progress: Float = 0f,
    val bytesDownloaded: Long = 0,
    val totalBytes: Long = 0
)

class RomSelector {

    private val _installedRoms = MutableStateFlow<List<RomInfo>>(emptyList())
    val installedRoms: StateFlow<List<RomInfo>> = _installedRoms.asStateFlow()

    private val _selectedRom = MutableStateFlow<RomInfo?>(null)
    val selectedRom: StateFlow<RomInfo?> = _selectedRom.asStateFlow()

    private val _downloadProgress = MutableStateFlow(RomDownloadProgress(RomStatus.NOT_DOWNLOADED))
    val downloadProgress: StateFlow<RomDownloadProgress> = _downloadProgress.asStateFlow()

    private val _availableRoms = MutableStateFlow(RomVariant.AVAILABLE_VARIANTS)
    val availableRoms: StateFlow<List<RomVariant>> = _availableRoms.asStateFlow()

    /**
     * Download a ROM variant
     */
    suspend fun downloadRom(variant: RomVariant): Result<RomInfo> {
        _downloadProgress.value = RomDownloadProgress(RomStatus.DOWNLOADING, 0f)

        try {
            // Simulate download progress
            for (i in 1..10) {
                _downloadProgress.value = RomDownloadProgress(
                    status = RomStatus.DOWNLOADING,
                    progress = i / 10f,
                    bytesDownloaded = (variant.sizeMB * 1024L * 1024L * i / 10),
                    totalBytes = variant.sizeMB * 1024L * 1024L
                )
                kotlinx.coroutines.delay(500)
            }

            // Extract ROM
            _downloadProgress.value = RomDownloadProgress(RomStatus.EXTRACTING, 0f)
            kotlinx.coroutines.delay(2000)

            val romInfo = RomInfo(
                id = variant.id,
                name = variant.displayName,
                version = "1.0",
                androidVersion = variant.displayName.split(" ").getOrNull(1) ?: "8.1",
                size = variant.sizeMB * 1024L * 1024L,
                checksum = "sha256:${System.currentTimeMillis()}",
                isDownloaded = true,
                isDefault = _installedRoms.value.isEmpty(),
                features = getVariantFeatures(variant.id)
            )

            _installedRoms.value = _installedRoms.value + romInfo
            _downloadProgress.value = RomDownloadProgress(RomStatus.READY, 1f)

            return Result.success(romInfo)
        } catch (e: Exception) {
            _downloadProgress.value = RomDownloadProgress(RomStatus.ERROR)
            return Result.failure(e)
        }
    }

    /**
     * Select a ROM to use
     */
    fun selectRom(romId: String): Result<RomInfo> {
        val rom = _installedRoms.value.find { it.id == romId }
            ?: return Result.failure(IllegalArgumentException("ROM not found"))

        _selectedRom.value = rom
        return Result.success(rom)
    }

    /**
     * Delete an installed ROM
     */
    fun deleteRom(romId: String): Result<Unit> {
        val rom = _installedRoms.value.find { it.id == romId }
            ?: return Result.failure(IllegalArgumentException("ROM not found"))

        if (rom.isDefault && _installedRoms.value.size > 1) {
            return Result.failure(IllegalStateException("Cannot delete default ROM"))
        }

        _installedRoms.value = _installedRoms.value.filter { it.id != romId }

        if (_selectedRom.value?.id == romId) {
            _selectedRom.value = _installedRoms.value.firstOrNull()
        }

        return Result.success(Unit)
    }

    /**
     * Set a ROM as default
     */
    fun setAsDefault(romId: String): Result<Unit> {
        val rom = _installedRoms.value.find { it.id == romId }
            ?: return Result.failure(IllegalArgumentException("ROM not found"))

        _installedRoms.value = _installedRoms.value.map {
            it.copy(isDefault = it.id == romId)
        }

        return Result.success(Unit)
    }

    /**
     * Import a custom ROM from file
     */
    suspend fun importRom(file: File): Result<RomInfo> {
        if (!file.exists()) {
            return Result.failure(IllegalArgumentException("File not found"))
        }

        _downloadProgress.value = RomDownloadProgress(RomStatus.EXTRACTING, 0f)

        // Simulate extraction
        kotlinx.coroutines.delay(2000)

        val romInfo = RomInfo(
            id = "custom_${System.currentTimeMillis()}",
            name = file.nameWithoutExtension,
            version = "custom",
            androidVersion = "Custom",
            size = file.length(),
            checksum = "sha256:custom",
            isDownloaded = true,
            isDefault = _installedRoms.value.isEmpty()
        )

        _installedRoms.value = _installedRoms.value + romInfo
        _downloadProgress.value = RomDownloadProgress(RomStatus.READY, 1f)

        return Result.success(romInfo)
    }

    /**
     * Get recommended ROM for device
     */
    fun getRecommendedRom(): RomVariant? {
        val totalMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024)

        return when {
            totalMemory < 2048 -> RomVariant.AVAILABLE_VARIANTS.find { it.id == "android81_light" }
            totalMemory < 4096 -> RomVariant.AVAILABLE_VARIANTS.find { it.recommended }
            else -> RomVariant.AVAILABLE_VARIANTS.find { it.id == "android11_gaming" }
        }
    }

    private fun getVariantFeatures(variantId: String): List<String> {
        return when (variantId) {
            "android81_light" -> listOf("Fast boot", "Low memory", "No GApps")
            "android81_full" -> listOf("Google Services", "Full features")
            "android10_light" -> listOf("Scoped storage", "Dark theme", "Gestures")
            "android11_gaming" -> listOf("Gaming mode", "High performance", "Low latency")
            else -> emptyList()
        }
    }
}
