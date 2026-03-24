package com.dualverse.di

import android.content.Context
import com.dualverse.core.virtualization.VirtualMachineConfig
import com.dualverse.core.virtualization.VirtualMachineManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides core dependencies for DualVerse.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideVirtualMachineConfig(): VirtualMachineConfig {
        return VirtualMachineConfig(
            resourceConfig = VirtualMachineManager.ResourceConfig(
                memoryMB = 2048,
                cpuShares = 512,
                storageMB = 1024,
                gpuAcceleration = true
            ),
            enableDebugging = false
        )
    }

    @Provides
    @Singleton
    fun provideVirtualMachineManager(
        @ApplicationContext context: Context,
        config: VirtualMachineConfig
    ): VirtualMachineManager {
        return VirtualMachineManager(context, config)
    }
}
