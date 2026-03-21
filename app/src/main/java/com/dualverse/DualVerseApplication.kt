package com.dualverse

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Main Application class for DualVerse.
 * Initializes core components and dependency injection.
 */
@HiltAndroidApp
class DualVerseApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // Initialize logging
        initTimber()
        
        // Create notification channels
        createNotificationChannels()
        
        Timber.d("DualVerse Application initialized")
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In release builds, we could plant a custom tree for crash reporting
            Timber.plant(ReleaseTree())
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            // Virtualization Service Channel
            val virtualizationChannel = NotificationChannel(
                CHANNEL_VIRTUALIZATION,
                getString(R.string.channel_virtualization),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_virtualization_desc)
            }
            
            // Account Notification Channel
            val accountChannel = NotificationChannel(
                CHANNEL_ACCOUNT,
                getString(R.string.channel_account),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_account_desc)
            }
            
            // Game Status Channel
            val gameChannel = NotificationChannel(
                CHANNEL_GAME_STATUS,
                getString(R.string.channel_game_status),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_game_status_desc)
            }
            
            notificationManager.createNotificationChannels(
                listOf(virtualizationChannel, accountChannel, gameChannel)
            )
        }
    }

    /**
     * Custom Timber tree for release builds.
     * Logs only warnings and errors.
     */
    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == android.util.Log.WARN || priority == android.util.Log.ERROR) {
                // In a real app, send to crash reporting service
                // Crashlytics.log(priority, tag, message)
            }
        }
    }

    companion object {
        const val CHANNEL_VIRTUALIZATION = "virtualization"
        const val CHANNEL_ACCOUNT = "account"
        const val CHANNEL_GAME_STATUS = "game_status"
    }
}
