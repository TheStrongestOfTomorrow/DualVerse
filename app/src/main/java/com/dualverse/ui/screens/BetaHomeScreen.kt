package com.dualverse.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dualverse.ui.theme.*

// ============================================
// DualVerse Beta - Modern Gaming UI
// Completely redesigned from ground up
// ============================================

data class GameAccount(
    val id: String,
    val gameName: String,
    val accountName: String,
    val level: Int,
    val status: AccountStatus,
    val lastPlayed: String,
    val iconEmoji: String
)

enum class AccountStatus {
    ONLINE, OFFLINE, IN_GAME, SYNCING
}

@Composable
fun BetaHomeScreen(
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToGames: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DualVerseGradients.backgroundGradient)
    ) {
        // Animated Background Orbs
        BackgroundOrbs()

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            BetaHeader()

            // Content based on selected tab
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(300),
                label = "content_crossfade"
            ) { tab ->
                when (tab) {
                    0 -> HomeTabContent(
                        onNavigateToAccounts = onNavigateToAccounts,
                        onNavigateToGames = onNavigateToGames
                    )
                    1 -> AccountsTabContent()
                    2 -> GamesTabContent()
                    3 -> SettingsTabContent(onNavigateToSettings)
                }
            }

            // Bottom Navigation
            BetaBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

@Composable
fun BackgroundOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Purple Orb
        Box(
            modifier = Modifier
                .offset(x = (-50 + offset1).dp, y = (100 + offset1).dp)
                .size(300.dp)
                .blur(100.dp)
                .background(NeonPurple.copy(alpha = 0.15f), CircleShape)
        )

        // Blue Orb
        Box(
            modifier = Modifier
                .offset(x = (250 + offset2).dp, y = (400 + offset2).dp)
                .size(250.dp)
                .blur(80.dp)
                .background(NeonBlue.copy(alpha = 0.12f), CircleShape)
        )

        // Cyan Orb
        Box(
            modifier = Modifier
                .offset(x = (100).dp, y = (600).dp)
                .size(200.dp)
                .blur(70.dp)
                .background(NeonGreen.copy(alpha = 0.08f), CircleShape)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetaHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(DualVersePurple, DualVerseBlue)
                    ),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "DV",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "DualVerse",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NeonGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Beta",
                    fontSize = 12.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Notification Button
        BadgedBox(
            badge = {
                Badge(
                    containerColor = NeonPink,
                    modifier = Modifier.size(18.dp)
                ) {
                    Text("3", fontSize = 10.sp)
                }
            }
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .background(DarkSurfaceVariant, CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun HomeTabContent(
    onNavigateToAccounts: () -> Unit,
    onNavigateToGames: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Quick Actions
        item {
            QuickActionsRow()
        }

        // Active Accounts Section
        item {
            SectionHeader(
                title = "Active Sessions",
                actionText = "See All",
                onAction = onNavigateToAccounts
            )
        }

        item {
            ActiveAccountsList()
        }

        // Quick Games
        item {
            SectionHeader(
                title = "Quick Launch",
                actionText = "All Games",
                onAction = onNavigateToGames
            )
        }

        item {
            QuickGamesRow()
        }

        // Stats Card
        item {
            StatsCard()
        }

        // Bottom padding for nav
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun QuickActionsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AddCircle,
            label = "Clone App",
            gradient = DualVerseGradients.primaryGradient
        )
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.SwapHoriz,
            label = "Switch",
            gradient = DualVerseGradients.accentGradient
        )
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.DualScreen,
            label = "Dual Mode",
            gradient = DualVerseGradients.successGradient
        )
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    gradient: Brush
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(gradient, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onAction) {
            Text(
                text = actionText,
                fontSize = 14.sp,
                color = NeonBlue
            )
        }
    }
}

@Composable
fun ActiveAccountsList() {
    val accounts = remember {
        listOf(
            GameAccount("1", "Roblox", "Player_One", 156, AccountStatus.ONLINE, "2 min ago", "🎮"),
            GameAccount("2", "PUBG Mobile", "Sniper_Pro", 89, AccountStatus.IN_GAME, "Active now", "🔫"),
            GameAccount("3", "Free Fire", "FireKing99", 67, AccountStatus.SYNCING, "Syncing...", "🔥"),
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        accounts.forEach { account ->
            AccountCard(account = account)
        }
    }
}

@Composable
fun AccountCard(account: GameAccount) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        when (account.status) {
                            AccountStatus.ONLINE -> Brush.linearGradient(listOf(NeonGreen, DualVerseCyan))
                            AccountStatus.IN_GAME -> Brush.linearGradient(listOf(NeonPurple, NeonPink))
                            AccountStatus.SYNCING -> Brush.linearGradient(listOf(NeonBlue, DualVerseBlue))
                            AccountStatus.OFFLINE -> Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
                        },
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = account.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.gameName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = account.accountName,
                    fontSize = 13.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            // Status Indicator
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                when (account.status) {
                                    AccountStatus.ONLINE -> NeonGreen
                                    AccountStatus.IN_GAME -> NeonPurple
                                    AccountStatus.SYNCING -> NeonBlue
                                    AccountStatus.OFFLINE -> Color.Gray
                                },
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = account.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        color = when (account.status) {
                            AccountStatus.ONLINE -> NeonGreen
                            AccountStatus.IN_GAME -> NeonPurple
                            AccountStatus.SYNCING -> NeonBlue
                            AccountStatus.OFFLINE -> Color.Gray
                        }
                    )
                }
                Text(
                    text = account.lastPlayed,
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun QuickGamesRow() {
    val games = remember {
        listOf("🎮" to "Roblox", "🔫" to "PUBG", "🔥" to "Free Fire", "⚔️" to "MLBB", "🌟" to "Genshin")
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(games) { (emoji, name) ->
            QuickGameItem(emoji = emoji, name = name)
        }
    }
}

@Composable
fun QuickGameItem(emoji: String, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    DarkSurfaceVariant,
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF),
            maxLines = 1
        )
    }
}

@Composable
fun StatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DualVersePurple.copy(alpha = 0.3f),
                            DualVerseBlue.copy(alpha = 0.2f)
                        )
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Your Stats",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(value = "12", label = "Games")
                    StatItem(value = "8", label = "Accounts")
                    StatItem(value = "156h", label = "Played")
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF9CA3AF)
        )
    }
}

@Composable
fun AccountsTabContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Your Accounts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            SearchBar()
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Account items
        items(5) { index ->
            AccountCard(
                account = GameAccount(
                    id = index.toString(),
                    gameName = listOf("Roblox", "PUBG Mobile", "Free Fire", "Mobile Legends", "Genshin Impact")[index],
                    accountName = listOf("Player_One", "Sniper_Pro", "FireKing99", "MLGPlayer", "Traveler_X")[index],
                    level = listOf(156, 89, 67, 45, 120)[index],
                    status = AccountStatus.values().random(),
                    lastPlayed = listOf("2 min ago", "5 min ago", "1 hour ago", "Yesterday", "3 days ago")[index],
                    iconEmoji = listOf("🎮", "🔫", "🔥", "⚔️", "🌟")[index]
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun GamesTabContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Games",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            SearchBar()
        }

        // Game Categories
        item {
            Text(
                text = "Popular",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(6) { index ->
            GameListItem(
                name = listOf("Roblox", "PUBG Mobile", "Free Fire", "Mobile Legends", "Genshin Impact", "COD Mobile")[index],
                accounts = listOf(2, 1, 3, 1, 2, 1)[index],
                emoji = listOf("🎮", "🔫", "🔥", "⚔️", "🌟", "🎖️")[index]
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun GameListItem(name: String, accounts: Int, emoji: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Text(
                    text = "$accounts account${if (accounts != 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF4B5563)
            )
        }
    }
}

@Composable
fun SettingsTabContent(onNavigateToSettings: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Settings Items
        item {
            SettingsItem(icon = Icons.Default.Person, title = "Account", subtitle = "Manage your DualVerse account")
        }
        item {
            SettingsItem(icon = Icons.Default.Security, title = "Security", subtitle = "Privacy & data settings")
        }
        item {
            SettingsItem(icon = Icons.Default.Palette, title = "Appearance", subtitle = "Theme & display options")
        }
        item {
            SettingsItem(icon = Icons.Default.Storage, title = "Storage", subtitle = "ROM & cache management")
        }
        item {
            SettingsItem(icon = Icons.Default.BugReport, title = "Debug Mode", subtitle = "Developer options")
        }
        item {
            SettingsItem(icon = Icons.Default.Info, title = "About", subtitle = "DualVerse Beta v1.0.0")
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = NeonBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Text(text = subtitle, fontSize = 13.sp, color = Color(0xFF9CA3AF))
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF4B5563)
            )
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Search...", color = Color(0xFF6B7280))
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6B7280))
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = DarkSurfaceVariant,
            focusedContainerColor = DarkSurfaceVariant,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = DualVersePurple
        )
    )
}

@Composable
fun BetaBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = DarkSurface,
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(Icons.Default.Home, contentDescription = null)
            },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DualVersePurple,
                selectedTextColor = DualVersePurple,
                indicatorColor = DualVersePurple.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            label = { Text("Accounts", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DualVersePurple,
                selectedTextColor = DualVersePurple,
                indicatorColor = DualVersePurple.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(Icons.Default.Gamepad, contentDescription = null)
            },
            label = { Text("Games", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DualVersePurple,
                selectedTextColor = DualVersePurple,
                indicatorColor = DualVersePurple.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(Icons.Default.Settings, contentDescription = null)
            },
            label = { Text("Settings", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DualVersePurple,
                selectedTextColor = DualVersePurple,
                indicatorColor = DualVersePurple.copy(alpha = 0.15f)
            )
        )
    }
}
