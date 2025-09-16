package com.hellogerman.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.hellogerman.app.gamification.*
import com.hellogerman.app.ui.components.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.hellogerman.app.ui.theme.*
import com.hellogerman.app.ui.viewmodel.MainViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.res.stringResource
import com.hellogerman.app.R
import com.hellogerman.app.ads.BannerAd1
import com.hellogerman.app.ads.AdMobManager
import com.hellogerman.app.data.DatabaseInitializer
import com.hellogerman.app.utils.NetworkUtils
import com.hellogerman.app.ui.utils.ResponsiveUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    val userProgress by mainViewModel.userProgress.collectAsState()
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(ResponsiveUtils.getResponsivePadding()),
        verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.getResponsiveSpacing())
    ) {
        item {
            // Header
            Column {
                Text(
                    text = "Willkommen!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Level ${userProgress?.currentLevel ?: "A1"}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Offline status indicator
                val context = LocalContext.current
                val isOnline = remember { mutableStateOf(NetworkUtils.isNetworkAvailable(context)) }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                                            Icon(
                            imageVector = if (isOnline.value) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                            contentDescription = if (isOnline.value) "Device is online with full internet connectivity" else "Device is offline with limited functionality",
                            tint = if (isOnline.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOnline.value) "Online - Full functionality available" else "Offline - Limited functionality",
                        fontSize = 12.sp,
                        color = if (isOnline.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        item {
            // Vocabulary Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val vocabularyCount by mainViewModel.vocabularyCount.collectAsState()
                
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Vocabulary",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = vocabularyCount.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Words Saved",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { navController.navigate("vocabulary") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "View Vocabulary",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
        
        item {
            GamificationStatsSection(mainViewModel, navController)
        }
        
        item {
            DailyChallengeSection(navController)
        }
        
        item {
            // Skills Progress
            Text(
                text = "Your Progress",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            SkillsProgressSection(
                userProgress = userProgress,
                onSkillClick = { skill ->
                    // Show interstitial ad before navigating to lesson screen
                    if (context is android.app.Activity) {
                        AdMobManager.showInterstitialAd(context)
                    }
                    navController.navigate(skill)
                }
            )
        }
        
        item {
            // Quick Actions
            Text(
                text = "Quick Actions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            QuickActionsSection(navController, context)
        }
        
        item {
            AchievementPreviewSection(mainViewModel, navController)
        }
        
        // Banner Ad
        item {
            BannerAd1(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SkillsProgressSection(
    userProgress: com.hellogerman.app.data.entities.UserProgress?,
    onSkillClick: (String) -> Unit
) {
    val skills = listOf(
        SkillProgress("Lesen", "lesen", LesenColor, userProgress?.lesenScore ?: 0),
        SkillProgress("Hören", "hoeren", HoerenColor, userProgress?.hoerenScore ?: 0),
        SkillProgress("Schreiben", "schreiben", SchreibenColor, userProgress?.schreibenScore ?: 0),
        SkillProgress("Sprechen", "sprechen", SprechenColor, userProgress?.sprechenScore ?: 0),
        SkillProgress("Grammar", "grammar", GrammarColor, userProgress?.grammarScore ?: 0)
    )
    
    val isTablet = ResponsiveUtils.isTablet()
    val isLandscape = ResponsiveUtils.isLandscape()
    
    if (isTablet && isLandscape) {
        // Tablet landscape - show skills in a grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.getResponsiveSpacing()),
            horizontalArrangement = Arrangement.spacedBy(ResponsiveUtils.getResponsiveSpacing())
        ) {
            items(skills.size) { index ->
                val skill = skills[index]
                SkillProgressCard(
                    skill = skill,
                    onClick = { onSkillClick(skill.route) }
                )
            }
        }
    } else {
        // Phone or tablet portrait - show skills in a column
        Column(
            verticalArrangement = Arrangement.spacedBy(ResponsiveUtils.getResponsiveSpacing())
        ) {
            skills.forEach { skill ->
                SkillProgressCard(
                    skill = skill,
                    onClick = { onSkillClick(skill.route) }
                )
            }
        }
    }
}

@Composable
fun SkillProgressCard(
    skill: SkillProgress,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                onClickLabel = "Open ${skill.name} lessons"
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(skill.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = skill.name.first().toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = skill.color
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = skill.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (skill.score / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = skill.color,
                    trackColor = skill.color.copy(alpha = 0.2f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "${skill.score}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = skill.color
            )
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController, context: android.content.Context) {
    val quickActions = listOf(
        QuickAction("Dictionary", Icons.Default.Translate, "dictionary"),
        QuickAction("My Vocabulary", Icons.Default.Bookmark, "vocabulary"),
        QuickAction("Achievements", Icons.Default.EmojiEvents, "gamification"),
        QuickAction("Progress Analytics", Icons.Default.Info, "progress"),
        QuickAction("Settings", Icons.Default.Settings, "settings")
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // First row with Dictionary and My Vocabulary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            quickActions.take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    onClick = { 
                        // Show interstitial ad before navigating to quick action
                        if (context is android.app.Activity) {
                            AdMobManager.showInterstitialAd(context)
                        }
                        navController.navigate(action.route) 
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Second row with Achievements and Progress Analytics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            quickActions.drop(2).take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    onClick = { 
                        // Show interstitial ad before navigating to quick action
                        if (context is android.app.Activity) {
                            AdMobManager.showInterstitialAd(context)
                        }
                        navController.navigate(action.route) 
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Third row with Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            quickActions.drop(4).forEach { action ->
                QuickActionCard(
                    action = action,
                    onClick = { 
                        // Show interstitial ad before navigating to quick action
                        if (context is android.app.Activity) {
                            AdMobManager.showInterstitialAd(context)
                        }
                        navController.navigate(action.route) 
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

data class SkillProgress(
    val name: String,
    val route: String,
    val color: androidx.compose.ui.graphics.Color,
    val score: Int
)

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String
)


