package com.dualverse.core.container

import android.content.Context
import android.util.Log
import com.dualverse.core.rom.RomManager
import java.io.File

/**
 * Loads and manages the containerized Android environment.
 * This class handles the native container initialization using the loader library.
 */
object ContainerLoader {

    private const val TAG = "ContainerLoader"
    private const val LOADER_LIB = "loader"
    private const val TWOYI_LIB = "twoyi"

    private var isLoaded = false
    private var containerPid: Int = 0

    /**
     * Initializes the container environment.
     * Must be called after ROM extraction is complete.
     */
    fun init(context: Context): Result<Unit> {
        if (isLoaded) {
            Log.w(TAG, "Container already loaded")
            return Result.success(Unit)
        }

        return try {
            Log.i(TAG, "Initializing container...")

            // Ensure boot files are ready
            ensureBootFiles(context)

            // Create loader symlink
            RomManager.createLoaderSymlink(context)

            // Load native libraries in order
            System.loadLibrary("p7zip")
            System.loadLibrary(LOADER_LIB)
            System.loadLibrary(TWOYI_LIB)

            Log.i(TAG, "Native libraries loaded successfully")
            isLoaded = true

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize container", e)
            Result.failure(e)
        }
    }

    /**
     * Starts the containerized Android system.
     */
    fun start(context: Context): Result<Int> {
        if (!isLoaded) {
            val initResult = init(context)
            if (initResult.isFailure) {
                return Result.failure(initResult.exceptionOrNull()!!)
            }
        }

        return try {
            Log.i(TAG, "Starting container...")

            val rootfsDir = RomManager.getRootfsDir(context)
            val loaderPath = File(context.applicationInfo.nativeLibraryDir, "libloader.so").absolutePath

            // Call native start
            val pid = nativeStartContainer(
                rootfsPath = rootfsDir.absolutePath,
                loaderPath = loaderPath,
                dataPath = context.dataDir.absolutePath
            )

            if (pid > 0) {
                containerPid = pid
                Log.i(TAG, "Container started with PID: $pid")
                Result.success(pid)
            } else {
                Result.failure(Exception("Failed to start container: native returned $pid"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start container", e)
            Result.failure(e)
        }
    }

    /**
     * Stops the containerized Android system.
     */
    fun stop(): Result<Unit> {
        if (containerPid <= 0) {
            return Result.success(Unit)
        }

        return try {
            Log.i(TAG, "Stopping container (PID: $containerPid)...")
            
            nativeStopContainer(containerPid)
            
            containerPid = 0
            Log.i(TAG, "Container stopped")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop container", e)
            Result.failure(e)
        }
    }

    /**
     * Installs an APK into the container.
     */
    fun installApp(apkPath: String, packageName: String): Result<Unit> {
        return try {
            Log.i(TAG, "Installing app: $packageName")
            
            val result = nativeInstallApp(apkPath, packageName)
            
            if (result == 0) {
                Log.i(TAG, "App installed successfully: $packageName")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Installation failed with code: $result"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to install app: $packageName", e)
            Result.failure(e)
        }
    }

    /**
     * Launches an app in the container.
     */
    fun launchApp(packageName: String): Result<Unit> {
        return try {
            Log.i(TAG, "Launching app: $packageName")
            
            nativeLaunchApp(packageName)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app: $packageName", e)
            Result.failure(e)
        }
    }

    /**
     * Gets the container status.
     */
    fun getStatus(): ContainerStatus {
        return when {
            !isLoaded -> ContainerStatus.NOT_LOADED
            containerPid <= 0 -> ContainerStatus.STOPPED
            else -> ContainerStatus.RUNNING
        }
    }

    /**
     * Ensures required boot files and directories exist.
     */
    private fun ensureBootFiles(context: Context) {
        val rootfsDir = RomManager.getRootfsDir(context)
        val devDir = File(rootfsDir, "dev")

        // Create device directories
        File(devDir, "input").mkdirs()
        File(devDir, "socket").mkdirs()
        File(devDir, "maps").mkdirs()

        // Create socket directory for IPC
        File(context.dataDir, "socket").mkdirs()

        // Kill any orphan processes from previous runs
        killOrphanProcesses()

        Log.d(TAG, "Boot files ensured")
    }

    /**
     * Kills orphan processes from previous container runs.
     */
    private fun killOrphanProcesses() {
        try {
            val process = Runtime.getRuntime().exec(
                "ps -ef | awk '{if(\$3==1) print \$2}' | xargs kill -9 2>/dev/null"
            )
            process.waitFor()
        } catch (e: Exception) {
            Log.d(TAG, "No orphan processes to kill")
        }
    }

    // Native methods - implemented by libloader.so and libtwoyi.so
    private external fun nativeStartContainer(
        rootfsPath: String,
        loaderPath: String,
        dataPath: String
    ): Int

    private external fun nativeStopContainer(pid: Int)
    private external fun nativeInstallApp(apkPath: String, packageName: String): Int
    private external fun nativeLaunchApp(packageName: String)
}

enum class ContainerStatus {
    NOT_LOADED,
    STOPPED,
    STARTING,
    RUNNING,
    ERROR
}
