package com.hellogerman.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.*
import com.hellogerman.app.data.entities.*
import com.hellogerman.app.gamification.*
import com.hellogerman.app.ui.components.*
import com.hellogerman.app.ui.theme.*
import com.hellogerman.app.ui.viewmodel.*
import com.hellogerman.app.ui.animations.enhancedPressAnimation
import com.hellogerman.app.ui.animations.entranceAnimation
import com.hellogerman.app.ui.animations.floatingAnimation
import com.hellogerman.app.ui.animations.pulseAnimation
import com.hellogerman.app.ui.animations.animatedGradient
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDashboardScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val gamificationManager = remember { GameificationManager(context) }
    
    // State variables
    var userLevel by remember { mutableStateOf<UserLevel?>(null) }
    var userStats by remember { mutableStateOf<UserStats?>(null) }
    var dailyChallenges by remember { mutableStateOf<List<DailyChallenge>>(emptyList()) }
    var recentAchievements by remember { mutableStateOf<List<Achievement>>(emptyList()) }
    var showLevelUpDialog by remember { mutableStateOf(false) }
    var showXPGain by remember { mutableStateOf(false) }
    var lastXPGain by remember { mutableStateOf(0L) }
    
    // Animated values
    val headerScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val cardOffset by animateIntAsState(
        targetValue = 0,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )
    
    // Initialize gamification system
    LaunchedEffect(Unit) {
        gamificationManager.initializeGameification()
        // Load user data (you'll need to implement these in GameificationManager)
        // userLevel = gamificationManager.getUserLevel()
        // userStats = gamificationManager.getUserStats()
        // dailyChallenges = gamificationManager.getTodaysChallenges()
        // recentAchievements = gamificationManager.getRecentAchievements()
    }
    
    // Beautiful gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        // Animated background particles
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawBackgroundParticles(this)
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Header with Level Info
            item {
                WelcomeHeader(
                    userLevel = userLevel,
                    userStats = userStats,
                    modifier = Modifier
                        .scale(headerScale)
                        .offset(y = cardOffset.dp)
                )
            }
            
            // Daily Challenges Section
            if (dailyChallenges.isNotEmpty()) {
                item {
                    Text(
                        text = "🎯 Today's Challenges",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(dailyChallenges) { challenge ->
                    DailyChallengeCard(
                        challenge = challenge,
                        modifier = Modifier
                    )
                }
            }
            
            // Quick Stats Row
            item {
                QuickStatsRow(
                    userStats = userStats,
                    modifier = Modifier.offset(y = (cardOffset / 2).dp)
                )
            }
            
            // Recent Achievements
            if (recentAchievements.isNotEmpty()) {
                item {
                    Text(
                        text = "🏆 Recent Achievements",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(recentAchievements) { achievement ->
                            CompactAchievementCard(achievement)
                        }
                    }
                }
            }
            
            // Skill Progress Section
            item {
                SkillProgressSection(
                    navController = navController,
                    modifier = Modifier.offset(y = (cardOffset / 3).dp)
                )
            }
            
            // Continue Learning Section
            item {
                ContinueLearningSection(
                    navController = navController,
                    modifier = Modifier.offset(y = (cardOffset / 4).dp)
                )
            }
        }
        
        // Floating Action Button for Quick Actions
        FloatingActionButton(
            onClick = { 
                // Quick start a lesson or show menu
                navController.navigate("lessons")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Quick Start",
                modifier = Modifier.size(28.dp)
            )
        }
    }
    
    // XP Gain Notification
    XPGainNotification(
        xpAmount = lastXPGain,
        isVisible = showXPGain,
        onDismiss = { showXPGain = false },
        modifier = Modifier.fillMaxWidth()
    )
    
    // Level Up Celebration
    if (showLevelUpDialog && userLevel != null) {
        LevelUpCelebration(
            newLevel = userLevel!!.level,
            newTitle = userLevel!!.title,
            isVisible = showLevelUpDialog,
            onDismiss = { showLevelUpDialog = false }
        )
    }
}

@Composable
private fun WelcomeHeader(
    userLevel: UserLevel?,
    userStats: UserStats?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box {
            // Background pattern
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawWelcomePattern(this)
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = getGreeting(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Text(
                            text = userLevel?.title ?: "Beginner",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    
                    // Level circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LVL",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "${userLevel?.level ?: 1}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // XP Progress Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP Progress",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${userLevel?.currentLevelXP ?: 0} / ${userLevel?.nextLevelXP ?: 100}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ProgressBar(
                        progress = if (userLevel?.nextLevelXP != null && userLevel.nextLevelXP > 0) {
                            (userLevel.currentLevelXP.toFloat() / userLevel.nextLevelXP)
                        } else 0f,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    userStats: UserStats?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Streak",
            value = "${userStats?.currentStreak ?: 0}",
            icon = "🔥",
            color = Color(0xFFFF5722),
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "Points",
            value = "${userStats?.totalPoints ?: 0}",
            icon = "⭐",
            color = Color(0xFFFFC107),
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "Accuracy",
            value = "${((userStats?.averageAccuracy ?: 0f) * 100).toInt()}%",
            icon = "🎯",
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompactAchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val rarityColor = when (achievement.rarity) {
        AchievementRarity.COMMON -> Color(0xFF8D6E63)
        AchievementRarity.RARE -> Color(0xFF90A4AE)
        AchievementRarity.EPIC -> Color(0xFFFFB74D)
        AchievementRarity.LEGENDARY -> Color(0xFFE1BEE7)
    }
    
    Card(
        modifier = modifier.width(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = rarityColor.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = achievement.icon,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SkillProgressSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "📊 Your Progress",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Skill progress items
            val skills = listOf(
                DashboardSkillProgress("Reading", "lesen", 75, Color(0xFF2196F3)),
                DashboardSkillProgress("Listening", "hoeren", 60, Color(0xFF4CAF50)),
                DashboardSkillProgress("Writing", "schreiben", 45, Color(0xFFFF9800)),
                DashboardSkillProgress("Speaking", "sprechen", 30, Color(0xFFE91E63)),
                DashboardSkillProgress("Grammar", "grammar", 85, Color(0xFF9C27B0))
            )
            
            skills.forEach { skill ->
                SkillProgressItem(
                    skill = skill,
                    onClick = { navController.navigate(skill.route) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SkillProgressItem(
    skill: DashboardSkillProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(skill.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (skill.route) {
                    "lesen" -> Icons.Default.MenuBook
                    "hoeren" -> Icons.Default.Headphones
                    "schreiben" -> Icons.Default.Edit
                    "sprechen" -> Icons.Default.Mic
                    "grammar" -> Icons.Default.School
                    else -> Icons.Default.School
                },
                contentDescription = skill.name,
                tint = skill.color,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${skill.progress}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = skill.color,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            ProgressBar(
                progress = skill.progress / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = skill.color,
                strokeWidth = 6f
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Go to ${skill.name}",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ContinueLearningSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "🚀 Continue Learning",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Ready for your next challenge?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("lessons") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Lesson")
                }
                
                OutlinedButton(
                    onClick = { navController.navigate("achievements") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Achievements")
                }
            }
        }
    }
}

// Helper functions
private fun getGreeting(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning!"
        hour < 17 -> "Good Afternoon!"
        else -> "Good Evening!"
    }
}

private fun drawBackgroundParticles(drawScope: DrawScope) {
    val particleCount = 20
    val colors = listOf(
        Color(0x1A2196F3), Color(0x1A4CAF50), Color(0x1AFF9800),
        Color(0x1AE91E63), Color(0x1A9C27B0)
    )
    
    repeat(particleCount) {
        val x = (0..drawScope.size.width.toInt()).random().toFloat()
        val y = (0..drawScope.size.height.toInt()).random().toFloat()
        val radius = (2..8).random().toFloat()
        val color = colors.random()
        
        drawScope.drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )
    }
}

private fun drawWelcomePattern(drawScope: DrawScope) {
    val width = drawScope.size.width
    val height = drawScope.size.height
    
    // Draw subtle geometric pattern
    for (i in 0..10) {
        drawScope.drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = (width / 8) * (i + 1),
            center = Offset(width * 0.8f, height * 0.2f)
        )
    }
}

// Data classes
private data class DashboardSkillProgress(
    val name: String,
    val route: String,
    val progress: Int,
    val color: Color
)
