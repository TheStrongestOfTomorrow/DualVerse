package com.dualverse.core.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Floating Overlay Manager for Dual-Mode
 * Allows users to run two games side-by-side in floating windows
 *
 * BETA FEATURE - Experimental
 */
class FloatingOverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val _isOverlayActive = MutableStateFlow(false)
    val isOverlayActive: StateFlow<Boolean> = _isOverlayActive.asStateFlow()

    private val _activeWindows = MutableStateFlow<List<FloatingWindow>>(emptyList())
    val activeWindows: StateFlow<List<FloatingWindow>> = _activeWindows.asStateFlow()

    private var overlayView: View? = null
    private var primaryWindow: FloatingWindow? = null
    private var secondaryWindow: FloatingWindow? = null

    data class FloatingWindow(
        val id: String,
        val packageName: String,
        val title: String,
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        val isMinimized: Boolean = false
    )

    /**
     * Start dual-mode overlay
     */
    fun startDualMode(
        primaryPackage: String,
        secondaryPackage: String
    ): Boolean {
        if (_isOverlayActive.value) {
            return false
        }

        try {
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            primaryWindow = FloatingWindow(
                id = "primary",
                packageName = primaryPackage,
                title = "Account 1",
                x = 0,
                y = 100,
                width = screenWidth / 2 - 10,
                height = screenHeight - 200,
                isMinimized = false
            )

            secondaryWindow = FloatingWindow(
                id = "secondary",
                packageName = secondaryPackage,
                title = "Account 2",
                x = screenWidth / 2 + 10,
                y = 100,
                width = screenWidth / 2 - 10,
                height = screenHeight - 200,
                isMinimized = false
            )

            _activeWindows.value = listOfNotNull(primaryWindow, secondaryWindow)
            _isOverlayActive.value = true

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun stopDualMode() {
        try {
            overlayView?.let {
                windowManager.removeView(it)
            }
            overlayView = null
            primaryWindow = null
            secondaryWindow = null
            _activeWindows.value = emptyList()
            _isOverlayActive.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun minimizeWindow(windowId: String) {
        updateWindowState(windowId, isMinimized = true)
    }

    fun restoreWindow(windowId: String) {
        updateWindowState(windowId, isMinimized = false)
    }

    fun swapWindows() {
        val currentWindows = _activeWindows.value
        if (currentWindows.size == 2) {
            val swapped = currentWindows.mapIndexed { index, window ->
                if (index == 0) {
                    window.copy(x = currentWindows[1].x)
                } else {
                    window.copy(x = currentWindows[0].x)
                }
            }
            _activeWindows.value = swapped
        }
    }

    fun resizeWindow(windowId: String, width: Int, height: Int) {
        _activeWindows.value = _activeWindows.value.map {
            if (it.id == windowId) it.copy(width = width, height = height) else it
        }
    }

    fun moveWindow(windowId: String, x: Int, y: Int) {
        _activeWindows.value = _activeWindows.value.map {
            if (it.id == windowId) it.copy(x = x, y = y) else it
        }
    }

    private fun updateWindowState(windowId: String, isMinimized: Boolean) {
        _activeWindows.value = _activeWindows.value.map {
            if (it.id == windowId) it.copy(isMinimized = isMinimized) else it
        }
    }

    fun getRecommendedLayout(): DualModeLayout {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        return when {
            screenWidth < 1080 -> DualModeLayout.STACKED
            screenWidth < 1440 -> DualModeLayout.SPLIT_50_50
            else -> DualModeLayout.SPLIT_60_40
        }
    }

    enum class DualModeLayout {
        STACKED,
        SPLIT_50_50,
        SPLIT_60_40,
        PIP
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }
}
