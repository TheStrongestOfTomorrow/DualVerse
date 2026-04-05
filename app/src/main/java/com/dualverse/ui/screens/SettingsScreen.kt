package com.dualverse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // VM Settings Section
            SettingsSection(title = "Virtual Machine") {
                SettingsItem(
                    icon = Icons.Outlined.Memory,
                    title = "Memory Allocation",
                    subtitle = "${uiState.memoryAllocation}MB",
                    onClick = { viewModel.showMemoryDialog() }
                )
                SettingsItem(
                    icon = Icons.Outlined.Speed,
                    title = "CPU Cores",
                    subtitle = "${uiState.cpuCores} cores",
                    onClick = { viewModel.showCpuDialog() }
                )
                SettingsSwitch(
                    icon = Icons.Outlined.VideogameAsset,
                    title = "GPU Acceleration",
                    subtitle = "Enable hardware rendering",
                    checked = uiState.gpuAcceleration,
                    onCheckedChange = { viewModel.setGpuAcceleration(it) }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Security Settings Section
            SettingsSection(title = "Security") {
                SettingsSwitch(
                    icon = Icons.Outlined.Fingerprint,
                    title = "Biometric Lock",
                    subtitle = "Require authentication to open app",
                    checked = uiState.biometricLock,
                    onCheckedChange = { viewModel.setBiometricLock(it) }
                )
                SettingsSwitch(
                    icon = Icons.Outlined.Security,
                    title = "Anti-Detection",
                    subtitle = "Bypass game detection systems",
                    checked = uiState.antiDetection,
                    onCheckedChange = { viewModel.setAntiDetection(it) }
                )
                SettingsSwitch(
                    icon = Icons.Outlined.HideSource,
                    title = "MAC Randomization",
                    subtitle = "Randomize network address",
                    checked = uiState.macRandomization,
                    onCheckedChange = { viewModel.setMacRandomization(it) }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Storage Settings Section
            SettingsSection(title = "Storage") {
                SettingsItem(
                    icon = Icons.Outlined.Storage,
                    title = "Clear Cache",
                    subtitle = "Free up ${uiState.cacheSize}",
                    onClick = { viewModel.clearCache() }
                )
                SettingsItem(
                    icon = Icons.Outlined.DeleteSweep,
                    title = "Clear All Data",
                    subtitle = "Remove all accounts and clones",
                    onClick = { viewModel.showClearDataDialog() }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // About Section
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = "Version",
                    subtitle = uiState.version,
                    onClick = {}
                )
                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = "License",
                    subtitle = "MIT License",
                    onClick = { viewModel.showLicense() }
                )
                SettingsItem(
                    icon = Icons.Outlined.Code,
                    title = "GitHub",
                    subtitle = "View source code",
                    onClick = { viewModel.openGitHub() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

data class SettingsUiState(
    val memoryAllocation: Int = 2048,
    val cpuCores: Int = 4,
    val gpuAcceleration: Boolean = true,
    val biometricLock: Boolean = false,
    val antiDetection: Boolean = true,
    val macRandomization: Boolean = true,
    val cacheSize: String = "128 MB",
    val version: String = "1.0.0"
)
