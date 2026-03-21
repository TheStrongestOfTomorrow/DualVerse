package com.dualverse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    viewModel: GamesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(GameFilter.ALL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Games") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == GameFilter.ALL,
                    onClick = { selectedFilter = GameFilter.ALL },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == GameFilter.INSTALLED,
                    onClick = { selectedFilter = GameFilter.INSTALLED },
                    label = { Text("Installed") }
                )
                FilterChip(
                    selected = selectedFilter == GameFilter.CLONED,
                    onClick = { selectedFilter = GameFilter.CLONED },
                    label = { Text("Cloned") }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.games) { game ->
                    GameCard(
                        game = game,
                        onLaunchClick = { viewModel.launchGame(game.packageName) },
                        onCloneClick = { viewModel.cloneGame(game.packageName) },
                        onRemoveClick = { viewModel.removeClone(game.packageName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GameCard(
    game: GameItem,
    onLaunchClick: () -> Unit,
    onCloneClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game Icon Placeholder
            Surface(
                modifier = Modifier.size(56.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Games,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (game.isCloned) "Cloned • ${game.accountCount} accounts" else "Installed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (game.isCloned) {
                    IconButton(onClick = onLaunchClick) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = "Launch")
                    }
                    IconButton(onClick = onRemoveClick) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Remove")
                    }
                } else {
                    FilledTonalButton(onClick = onCloneClick) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clone")
                    }
                }
            }
        }
    }
}

enum class GameFilter {
    ALL, INSTALLED, CLONED
}

data class GameItem(
    val packageName: String,
    val name: String,
    val isCloned: Boolean = false,
    val accountCount: Int = 0
)

data class GamesUiState(
    val games: List<GameItem> = emptyList(),
    val filter: GameFilter = GameFilter.ALL
)
