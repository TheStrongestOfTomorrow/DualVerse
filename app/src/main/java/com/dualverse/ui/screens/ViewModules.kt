package com.dualverse.ui.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        _uiState.update { it.copy(
            accounts = listOf(
                AccountInfo("1", "Main Account", "Roblox", true),
                AccountInfo("2", "Alt Account", "Roblox", false),
                AccountInfo("3", "PUBG Primary", "PUBG Mobile", false)
            )
        )}
    }

    fun showAddAccountDialog() {
        // TODO: Implement
    }

    fun switchAccount(id: String) {
        // TODO: Implement
    }

    fun deleteAccount(id: String) {
        // TODO: Implement
    }

    fun launchAccount(id: String) {
        // TODO: Implement
    }
}

@HiltViewModel
class GamesViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    private fun loadGames() {
        _uiState.update { it.copy(
            games = listOf(
                GameItem("com.roblox.client", "Roblox", true, 2),
                GameItem("com.pubg.krmobile", "PUBG Mobile", true, 1),
                GameItem("com.dts.freefireth", "Free Fire", false, 0),
                GameItem("com.mobile.legends", "Mobile Legends", false, 0),
                GameItem("com.miHoYo.GenshinImpact", "Genshin Impact", false, 0)
            )
        )}
    }

    fun launchGame(packageName: String) {
        // TODO: Implement
    }

    fun cloneGame(packageName: String) {
        // TODO: Implement
    }

    fun removeClone(packageName: String) {
        // TODO: Implement
    }
}

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun showMemoryDialog() {
        // TODO: Implement
    }

    fun showCpuDialog() {
        // TODO: Implement
    }

    fun setGpuAcceleration(enabled: Boolean) {
        _uiState.update { it.copy(gpuAcceleration = enabled) }
    }

    fun setBiometricLock(enabled: Boolean) {
        _uiState.update { it.copy(biometricLock = enabled) }
    }

    fun setAntiDetection(enabled: Boolean) {
        _uiState.update { it.copy(antiDetection = enabled) }
    }

    fun setMacRandomization(enabled: Boolean) {
        _uiState.update { it.copy(macRandomization = enabled) }
    }

    fun clearCache() {
        // TODO: Implement
    }

    fun showClearDataDialog() {
        // TODO: Implement
    }

    fun showLicense() {
        // TODO: Implement
    }

    fun openGitHub() {
        // TODO: Implement
    }
}
