package com.dualverse.core.accounts

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages cloned accounts and their sessions within DualVerse.
 * This class is responsible for tracking installed clones, managing session state,
 * and providing data to the UI layer.
 */
@Singleton
class AccountManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Represents a cloned app account/instance.
     */
    data class ClonedAccount(
        val id: String,
        val packageName: String,
        val appName: String,
        val cloneIndex: Int,
        val lastUsed: Long,
        val sessionState: SessionState = SessionState.Stopped,
        val icon: ApplicationInfo? = null
    )

    /**
     * Represents the state of a clone session.
     */
    enum class SessionState {
        Stopped,
        Starting,
        Running,
        Paused,
        Error
    }

    // All cloned accounts
    private val _accounts = MutableStateFlow<List<ClonedAccount>>(emptyList())
    val accounts: StateFlow<List<ClonedAccount>> = _accounts.asStateFlow()

    // Active sessions (running instances)
    private val _activeSessions = MutableStateFlow<Map<String, ClonedAccount>>(emptyMap())
    val activeSessions: StateFlow<Map<String, ClonedAccount>> = _activeSessions.asStateFlow()

    init {
        loadAccounts()
    }

    /**
     * Loads all cloned accounts from storage.
     */
    private fun loadAccounts() {
        // TODO: Load from Room database
        _accounts.value = emptyList()
        Timber.d("Loaded ${_accounts.value.size} cloned accounts")
    }

    /**
     * Gets a list of installed games that can be cloned.
     */
    fun getInstalledGames(): List<GameInfo> {
        val pm = context.packageManager
        val games = mutableListOf<GameInfo>()
        
        val knownGamePackages = setOf(
            "com.roblox.client",
            "com.pubg.krmobile",
            "com.dts.freefireth",
            "com.mobile.legends",
            "com.miHoYo.GenshinImpact",
            "com.activision.callofduty.shooter",
            "com.supercell.clashofclans",
            "com.supercell.clashroyale",
            "com.riotgames.league.wildrift",
            "com.mojang.minecraftpe"
        )

        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        
        for (app in installedApps) {
            if (knownGamePackages.contains(app.packageName)) {
                val name = pm.getApplicationLabel(app).toString()
                games.add(GameInfo(
                    packageName = app.packageName,
                    name = name,
                    isInstalled = true,
                    icon = app
                ))
            }
        }

        return games
    }

    /**
     * Creates a new clone of the specified app.
     */
    fun createClone(packageName: String): Result<ClonedAccount> {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val appName = pm.getApplicationLabel(appInfo).toString()
            
            val existingClones = _accounts.value.filter { it.packageName == packageName }
            val cloneIndex = existingClones.size + 1
            
            val account = ClonedAccount(
                id = "${packageName}_clone_$cloneIndex",
                packageName = packageName,
                appName = "$appName (Clone $cloneIndex)",
                cloneIndex = cloneIndex,
                lastUsed = System.currentTimeMillis()
            )
            
            _accounts.value = _accounts.value + account
            Timber.i("Created clone: ${account.id}")
            
            Result.success(account)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create clone for: $packageName")
            Result.failure(e)
        }
    }

    /**
     * Removes a cloned account.
     */
    fun removeClone(accountId: String): Result<Unit> {
        return try {
            val account = _accounts.value.find { it.id == accountId }
                ?: return Result.failure(IllegalArgumentException("Account not found: $accountId"))
            
            _accounts.value = _accounts.value.filter { it.id != accountId }
            _activeSessions.value = _activeSessions.value - accountId
            
            Timber.i("Removed clone: $accountId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove clone: $accountId")
            Result.failure(e)
        }
    }

    /**
     * Starts a session for the given account.
     */
    fun startSession(accountId: String): Result<Unit> {
        val account = _accounts.value.find { it.id == accountId }
            ?: return Result.failure(IllegalArgumentException("Account not found: $accountId"))
        
        _activeSessions.value = _activeSessions.value + (accountId to account.copy(
            sessionState = SessionState.Running,
            lastUsed = System.currentTimeMillis()
        ))
        
        Timber.i("Started session: $accountId")
        return Result.success(Unit)
    }

    /**
     * Stops a running session.
     */
    fun stopSession(accountId: String): Result<Unit> {
        val session = _activeSessions.value[accountId]
            ?: return Result.failure(IllegalArgumentException("Session not found: $accountId"))
        
        _activeSessions.value = _activeSessions.value - accountId
        Timber.i("Stopped session: $accountId")
        return Result.success(Unit)
    }

    /**
     * Gets the number of active sessions.
     */
    fun getActiveSessionCount(): Int = _activeSessions.value.size

    /**
     * Gets the total number of cloned accounts.
     */
    fun getTotalCloneCount(): Int = _accounts.value.size
}

/**
 * Represents information about an installed game.
 */
data class GameInfo(
    val packageName: String,
    val name: String,
    val isInstalled: Boolean = true,
    val icon: ApplicationInfo? = null
)
