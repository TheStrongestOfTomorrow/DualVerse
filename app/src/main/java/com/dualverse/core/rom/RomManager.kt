package com.dualverse.core.rom

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*

/**
 * Manages the Android ROM extraction and initialization.
 * Based on the Twoyi container architecture.
 * 
 * The ROM is a minimal Android 8.1 system (~193MB compressed) that runs
 * as a containerized environment within the app.
 */
object RomManager {

    private const val TAG = "RomManager"
    private const val ROOTFS_NAME = "rootfs.7z"
    private const val ROM_INFO_FILE = "rom.ini"

    /**
     * ROM information structure.
     */
    data class RomInfo(
        val author: String = "unknown",
        val version: String = "unknown",
        val code: Long = 0,
        val md5: String = "",
        val description: String = ""
    ) {
        fun isValid(): Boolean = code > 0
    }

    /**
     * Checks if the ROM has been extracted and is ready.
     */
    fun isRomExtracted(context: Context): Boolean {
        val initFile = File(getRootfsDir(context), "init")
        return initFile.exists()
    }

    /**
     * Gets the rootfs directory.
     */
    fun getRootfsDir(context: Context): File {
        return File(context.dataDir, "rootfs")
    }

    /**
     * Gets the sdcard directory inside the container.
     */
    fun getRomSdcardDir(context: Context): File {
        return File(getRootfsDir(context), "sdcard")
    }

    /**
     * Gets the vendor directory for custom properties.
     */
    fun getVendorDir(context: Context): File {
        return File(getRootfsDir(context), "vendor")
    }

    /**
     * Gets the vendor properties file.
     */
    fun getVendorPropFile(context: Context): File {
        return File(getVendorDir(context), "default.prop")
    }

    /**
     * Gets ROM info from assets.
     */
    fun getRomInfoFromAssets(context: Context): RomInfo {
        return try {
            context.assets.open(ROM_INFO_FILE).use { input ->
                getRomInfo(input)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read ROM info from assets", e)
            RomInfo()
        }
    }

    /**
     * Gets current ROM info from extracted files.
     */
    fun getCurrentRomInfo(context: Context): RomInfo {
        val infoFile = File(getRootfsDir(context), ROM_INFO_FILE)
        return try {
            FileInputStream(infoFile).use { input ->
                getRomInfo(input)
            }
        } catch (e: Exception) {
            RomInfo()
        }
    }

    /**
     * Parses ROM info from input stream.
     */
    private fun getRomInfo(inputStream: InputStream): RomInfo {
        val props = Properties()
        props.load(inputStream)
        return RomInfo(
            author = props.getProperty("author", "unknown"),
            version = props.getProperty("version", "unknown"),
            code = props.getProperty("code", "0").toLongOrNull() ?: 0,
            md5 = props.getProperty("md5", ""),
            description = props.getProperty("desc", "")
        )
    }

    /**
     * Checks if ROM needs upgrade.
     */
    fun needsUpgrade(context: Context): Boolean {
        val currentRomInfo = getCurrentRomInfo(context)
        val assetRomInfo = getRomInfoFromAssets(context)
        return assetRomInfo.code > currentRomInfo.code
    }

    /**
     * Extracts the ROM from assets to the data directory.
     * This is a long-running operation and should be called from a background thread.
     */
    suspend fun extractRootfs(
        context: Context, 
        progressCallback: ((Int) -> Unit)? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Starting ROM extraction...")
            val startTime = System.currentTimeMillis()

            // Create directories
            ensureDirs(context)

            // Copy rootfs.7z from assets to temp file
            val rootfsFile = File(context.cacheDir, ROOTFS_NAME)
            progressCallback?.invoke(10)

            copyAssetToFile(context, ROOTFS_NAME, rootfsFile) { progress ->
                progressCallback?.invoke(10 + (progress * 0.3f).toInt())
            }

            progressCallback?.invoke(40)
            Log.i(TAG, "Copied rootfs.7z (${rootfsFile.length()} bytes)")

            // Extract using native library
            val result = extract7z(context, rootfsFile, context.dataDir)
            
            progressCallback?.invoke(95)

            // Cleanup temp file
            rootfsFile.delete()

            // Initialize vendor properties
            initVendorProps(context)

            // Copy ROM info
            copyAssetToFile(context, ROM_INFO_FILE, File(getRootfsDir(context), ROM_INFO_FILE), null)

            val elapsed = System.currentTimeMillis() - startTime
            Log.i(TAG, "ROM extraction completed in ${elapsed}ms, result: $result")

            progressCallback?.invoke(100)

            if (result == 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Extraction failed with code: $result"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract ROM", e)
            Result.failure(e)
        }
    }

    /**
     * Initializes vendor properties with device-specific settings.
     */
    private fun initVendorProps(context: Context) {
        val propFile = getVendorPropFile(context)
        propFile.parentFile?.mkdirs()

        val props = Properties().apply {
            setProperty("persist.sys.language", Locale.getDefault().language)
            setProperty("persist.sys.country", Locale.getDefault().country)
            setProperty("persist.sys.timezone", TimeZone.getDefault().id)
            setProperty("ro.sf.lcd_density", context.resources.displayMetrics.densityDpi.toString())
        }

        FileWriter(propFile).use { writer ->
            props.store(writer, "DualVerse Vendor Properties")
        }
    }

    /**
     * Ensures required directories exist.
     */
    private fun ensureDirs(context: Context) {
        val rootfsDir = getRootfsDir(context)
        val devDir = File(rootfsDir, "dev")
        
        File(devDir, "input").mkdirs()
        File(devDir, "socket").mkdirs()
        File(devDir, "maps").mkdirs()
        File(context.dataDir, "socket").mkdirs()
        File(context.dataDir, "rootfs").mkdirs()
    }

    /**
     * Copies an asset file to a destination file.
     */
    private fun copyAssetToFile(
        context: Context,
        assetName: String,
        destFile: File,
        progressCallback: ((Int) -> Unit)?
    ) {
        context.assets.open(assetName).use { input ->
            BufferedInputStream(input).use { bufferedInput ->
                FileOutputStream(destFile).use { output ->
                    BufferedOutputStream(output).use { bufferedOutput ->
                        val buffer = ByteArray(8192)
                        var totalRead = 0L
                        var read: Int
                        val totalSize = bufferedInput.available().toLong()

                        while (bufferedInput.read(buffer).also { read = it } > 0) {
                            bufferedOutput.write(buffer, 0, read)
                            totalRead += read
                            progressCallback?.invoke(((totalRead * 100) / totalSize).toInt())
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a symlink to the loader library.
     */
    fun createLoaderSymlink(context: Context) {
        val loaderFile = File(context.applicationInfo.nativeLibraryDir, "libloader.so")
        val symlinkFile = File(getRootfsDir(context), "libloader.so")
        if (symlinkFile.exists()) symlinkFile.delete()
        try {
            android.system.Os.symlink(loaderFile.absolutePath, symlinkFile.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create loader symlink", e)
        }
    }

    /**
     * Extracts 7z archive using native library.
     */
    private fun extract7z(context: Context, archive: File, destDir: File): Int {
        return try {
            nativeExtract7z(archive.absolutePath, destDir.absolutePath)
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Native library not loaded", e)
            -1
        }
    }

    /**
     * Clears the ROM data (for factory reset).
     */
    fun clearRomData(context: Context): Boolean {
        return try {
            val rootfsDir = getRootfsDir(context)
            deleteDirectory(rootfsDir)
            Log.i(TAG, "Cleared ROM data")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear ROM data", e)
            false
        }
    }

    private fun deleteDirectory(dir: File): Boolean {
        if (dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                deleteDirectory(file)
            }
        }
        return dir.delete()
    }

    private external fun nativeExtract7z(archivePath: String, destPath: String): Int

    init {
        try {
            System.loadLibrary("p7zip")
            Log.d(TAG, "Loaded p7zip native library")
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "p7zip native library not available")
        }
    }
}
