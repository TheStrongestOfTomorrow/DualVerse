package com.dualverse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AppPickerDialog(
    apps: List<AppInfo>,
    onAppSelected: (AppInfo) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select App to Clone",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                apps.forEach { app ->
                    TextButton(
                        onClick = { onAppSelected(app) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(app.name)
                    }
                }
            }
        }
    }
}

@Composable
fun SessionCard(
    session: SessionInfo,
    onSwitchClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(session.appName, style = MaterialTheme.typography.titleMedium)
                Text(session.accountName, style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                TextButton(onClick = onSwitchClick) {
                    Text("Switch")
                }
                TextButton(onClick = onStopClick) {
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
fun GameListItem(
    game: GameInfo,
    onLaunchClick: () -> Unit,
    onCloneClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(game.name, style = MaterialTheme.typography.titleMedium)
                Text(game.status, style = MaterialTheme.typography.bodySmall)
            }
            Row {
                TextButton(onClick = onLaunchClick) {
                    Text("Launch")
                }
                TextButton(onClick = onCloneClick) {
                    Text("Clone")
                }
            }
        }
    }
}
