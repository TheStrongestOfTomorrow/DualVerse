package com.dualverse.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dualverse.core.accounts.AccountManager
import com.dualverse.core.virtualization.VirtualMachineManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vmManager: VirtualMachineManager,
    private val accountManager: AccountManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeVmState()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load installed games
            val games = loadInstalledGames()
            _uiState.update { it.copy(
                installedGames = games,
                popularGames = getPopularGames(games)
            )}
        }
    }

    private fun observeVmState() {
        viewModelScope.launch {
            vmManager.state.collect { state ->
                val vmState = when (state) {
                    is VirtualMachineManager.State.Running -> VmState.RUNNING
                    is VirtualMachineManager.State.Starting -> VmState.STARTING
                    is VirtualMachineManager.State.Stopped -> VmState.STOPPED
                    is VirtualMachineManager.State.Error -> VmState.ERROR
                    else -> VmState.STOPPED
                }
                
                _uiState.update { it.copy(vmState = vmState) }
            }
        }
    }

    fun startVirtualMachine() {
        viewModelScope.launch {
            _uiState.update { it.copy(vmState = VmState.STARTING) }
            
            vmManager.startVirtualMachine()
                .onSuccess {
                    Timber.i("VM started successfully")
                    _uiState.update { it.copy(
                        vmState = VmState.RUNNING,
                        error = null
                    )}
                }
                .onFailure { error ->
                    Timber.e(error, "Failed to start VM")
                    _uiState.update { it.copy(
                        vmState = VmState.ERROR,
                        error = error.message
                    )}
                }
        }
    }

    fun stopVirtualMachine() {
        viewModelScope.launch {
            vmManager.stopVirtualMachine()
                .onSuccess {
                    Timber.i("VM stopped successfully")
                    _uiState.update { it.copy(vmState = VmState.STOPPED) }
                }
                .onFailure { error ->
                    Timber.e(error, "Failed to stop VM")
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun showAppPicker() {
        _uiState.update { it.copy(showAppPicker = true) }
    }

    fun hideAppPicker() {
        _uiState.update { it.copy(showAppPicker = false) }
    }

    fun cloneApp(packageName: String) {
        viewModelScope.launch {
            if (_uiState.value.vmState == VmState.STOPPED) {
                startVirtualMachine()
            }
            
            try {
                val instance = vmManager.createInstance(packageName)
                Timber.i("Created instance for: $packageName")
            } catch (e: Exception) {
                Timber.e(e, "Failed to create instance")
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun launchGame(packageName: String) {
        // TODO: Implement game launch
        Timber.d("Launch game: $packageName")
    }

    fun cloneGame(packageName: String) {
        cloneApp(packageName)
    }

    fun startDualInstance() {
        showAppPicker()
    }

    fun switchToSession(sessionId: String) {
        // TODO: Implement session switching
        Timber.d("Switch to session: $sessionId")
    }

    fun stopSession(sessionId: String) {
        viewModelScope.launch {
            vmManager.destroyInstance(sessionId)
                .onSuccess {
                    Timber.i("Stopped session: $sessionId")
                }
                .onFailure { error ->
                    Timber.e(error, "Failed to stop session")
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadInstalledGames(): List<AppInfo> {
        // TODO: Implement actual game detection
        return listOf(
            AppInfo("com.roblox.client", "Roblox"),
            AppInfo("com.pubg.krmobile", "PUBG Mobile"),
            AppInfo("com.dts.freefireth", "Free Fire"),
            AppInfo("com.mobile.legends", "Mobile Legends"),
            AppInfo("com.miHoYo.GenshinImpact", "Genshin Impact"),
            AppInfo("com.activision.callofduty.shooter", "COD Mobile")
        )
    }

    private fun getPopularGames(allGames: List<AppInfo>): List<GameInfo> {
        return allGames.map { app ->
            GameInfo(
                packageName = app.packageName,
                name = app.name,
                status = if (_uiState.value.vmState == VmState.RUNNING) "Ready" else "VM Offline"
            )
        }
    }
}
