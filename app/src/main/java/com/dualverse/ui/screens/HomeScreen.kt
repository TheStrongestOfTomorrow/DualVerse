package com.dualverse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dualverse.ui.theme.GradientColors

/**
 * Home screen displaying the main dashboard and quick actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAccounts: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            QuickActionsSheet(
                onCloneApp = { viewModel.showAppPicker() },
                onStartDual = { viewModel.startDualInstance() }
            )
        },
        sheetPeekHeight = 80.dp,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DualVerse",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
            item {
                StatusCard(
                    vmState = uiState.vmState,
                    activeInstances = uiState.activeInstances,
                    memoryUsage = uiState.memoryUsage
                )
            }

            // Quick Actions Section
            item {
                SectionHeader(
                    title = "Quick Actions",
                    actionIcon = Icons.Outlined.AddCircle,
                    onActionClick = { viewModel.showAppPicker() }
                )
            }

            item {
                QuickActionsRow(
                    onAccountsClick = onNavigateToAccounts,
                    onGamesClick = onNavigateToGames,
                    onCloneClick = { viewModel.showAppPicker() },
                    onDualClick = { viewModel.startDualInstance() }
                )
            }

            // Active Sessions Section
            if (uiState.activeSessions.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Active Sessions",
                        actionText = "See All",
                        onActionClick = onNavigateToAccounts
                    )
                }

                items(uiState.activeSessions) { session ->
                    SessionCard(
                        session = session,
                        onSwitchClick = { viewModel.switchToSession(session.id) },
                        onStopClick = { viewModel.stopSession(session.id) }
                    )
                }
            }

            // Supported Games Section
            item {
                SectionHeader(
                    title = "Popular Games",
                    actionText = "See All",
                    onActionClick = onNavigateToGames
                )
            }

            items(uiState.popularGames.take(4)) { game ->
                GameListItem(
                    game = game,
                    onLaunchClick = { viewModel.launchGame(game.packageName) },
                    onCloneClick = { viewModel.cloneGame(game.packageName) }
                )
            }

            // Spacer for bottom sheet
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // App Picker Dialog
    if (uiState.showAppPicker) {
        AppPickerDialog(
            apps = uiState.installedGames,
            onAppSelected = { app ->
                viewModel.cloneApp(app.packageName)
                viewModel.hideAppPicker()
            },
            onDismiss = { viewModel.hideAppPicker() }
        )
    }

    // Error handling
    AnimatedVisibility(
        visible = uiState.error != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(uiState.error ?: "")
        }
    }
}

@Composable
private fun StatusCard(
    vmState: VmState,
    activeInstances: Int,
    memoryUsage: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = when (vmState) {
                            VmState.RUNNING -> GradientColors.success
                            VmState.STARTING -> GradientColors.warning
                            VmState.STOPPED -> GradientColors.neutral
                            VmState.ERROR -> GradientColors.error
                        }
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (vmState) {
                                VmState.RUNNING -> "Virtual Machine Running"
                                VmState.STARTING -> "Starting Virtual Machine..."
                                VmState.STOPPED -> "Virtual Machine Stopped"
                                VmState.ERROR -> "Error Occurred"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$activeInstances active instances",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = memoryUsage,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onAccountsClick: () -> Unit,
    onGamesClick: () -> Unit,
    onCloneClick: () -> Unit,
    onDualClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Person,
            label = "Accounts",
            onClick = onAccountsClick
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Games,
            label = "Games",
            onClick = onGamesClick
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.ContentCopy,
            label = "Clone",
            onClick = onCloneClick
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.ViewColumn,
            label = "Dual",
            onClick = onDualClick
        )
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(text = actionText)
            }
        } else if (actionIcon != null && onActionClick != null) {
            IconButton(onClick = onActionClick) {
                Icon(actionIcon, contentDescription = "Action")
            }
        }
    }
}

@Composable
private fun QuickActionsSheet(
    onCloneApp: () -> Unit,
    onStartDual: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilledTonalButton(
                modifier = Modifier.weight(1f),
                onClick = onCloneApp
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clone App")
            }
            FilledTonalButton(
                modifier = Modifier.weight(1f),
                onClick = onStartDual
            ) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Dual")
            }
        }
    }
}

// Data classes for UI state
data class HomeUiState(
    val vmState: VmState = VmState.STOPPED,
    val activeInstances: Int = 0,
    val memoryUsage: String = "0%",
    val showAppPicker: Boolean = false,
    val installedGames: List<AppInfo> = emptyList(),
    val activeSessions: List<SessionInfo> = emptyList(),
    val popularGames: List<GameInfo> = emptyList(),
    val error: String? = null
)

enum class VmState {
    RUNNING, STARTING, STOPPED, ERROR
}

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Any? = null
)

data class SessionInfo(
    val id: String,
    val appName: String,
    val accountName: String,
    val status: String
)

data class GameInfo(
    val packageName: String,
    val name: String,
    val icon: Any? = null,
    val status: String = "Ready"
)
