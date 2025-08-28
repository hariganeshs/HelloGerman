package com.hellogerman.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.hellogerman.app.gamification.Achievement
import com.hellogerman.app.gamification.AchievementRarity
import kotlinx.coroutines.delay

/**
 * Engaging notification and celebration system for maximum user engagement
 */

enum class NotificationType {
    ACHIEVEMENT_UNLOCKED,
    LEVEL_UP,
    STREAK_MILESTONE,
    XP_GAINED,
    PERFECT_SCORE,
    DAILY_GOAL_COMPLETED,
    COINS_EARNED
}

data class GameNotification(
    val type: NotificationType,
    val title: String,
    val message: String,
    val icon: ImageVector,
    val color: Color,
    val duration: Long = 3000L,
    val xpGained: Int = 0,
    val coinsGained: Int = 0
)

@Composable
fun NotificationDisplay(
    notification: GameNotification?,
    onDismiss: () -> Unit
) {
    var visible by remember(notification) { mutableStateOf(notification != null) }
    
    LaunchedEffect(notification) {
        if (notification != null) {
            visible = true
            delay(notification.duration)
            visible = false
            delay(300) // Animation duration
            onDismiss()
        }
    }
    
    if (notification != null) {
        Popup(
            alignment = Alignment.TopCenter,
            properties = PopupProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = true
            )
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(300)
                ) + fadeOut()
            ) {
                NotificationCard(notification)
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: GameNotification) {
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 60.dp)
            .scale(animatedScale)
            .border(
                2.dp,
                notification.color,
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = notification.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Icon
            val iconScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            
            Icon(
                imageVector = notification.icon,
                contentDescription = notification.title,
                modifier = Modifier
                    .size(48.dp)
                    .scale(iconScale),
                tint = notification.color
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = notification.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            if (notification.xpGained > 0 || notification.coinsGained > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (notification.xpGained > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "XP",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFFFF9800)
                            )
                            Text(
                                text = " +${notification.xpGained} XP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                    
                    if (notification.xpGained > 0 && notification.coinsGained > 0) {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    
                    if (notification.coinsGained > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFFFFD700)
                            )
                            Text(
                                text = " +${notification.coinsGained}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCelebration(
    achievement: Achievement?,
    onDismiss: () -> Unit
) {
    var visible by remember(achievement) { mutableStateOf(achievement != null) }
    
    LaunchedEffect(achievement) {
        if (achievement != null) {
            visible = true
            delay(4000) // Longer duration for achievements
            visible = false
            delay(500)
            onDismiss()
        }
    }
    
    if (achievement != null) {
        Popup(
            alignment = Alignment.Center,
            properties = PopupProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = true
            )
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = scaleOut(
                    animationSpec = tween(300)
                ) + fadeOut()
            ) {
                AchievementCelebrationCard(achievement)
            }
        }
    }
}

@Composable
private fun AchievementCelebrationCard(achievement: Achievement) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val rarityColor = when (achievement.rarity) {
        AchievementRarity.LEGENDARY -> Color(0xFFFFD700)
        AchievementRarity.EPIC -> Color(0xFF9C27B0)
        AchievementRarity.RARE -> Color(0xFF2196F3)
        AchievementRarity.COMMON -> Color(0xFF4CAF50)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .border(
                3.dp,
                rarityColor.copy(alpha = glowAlpha),
                RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = rarityColor.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
    ) {
        Box {
            // Background sparkles effect
            repeat(6) { index ->
                val offset = remember { 
                    androidx.compose.ui.geometry.Offset(
                        (index * 60f) % 300f,
                        (index * 80f) % 400f
                    )
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier
                        .offset(
                            x = offset.x.dp,
                            y = offset.y.dp
                        )
                        .size(12.dp)
                        .scale(0.5f + (glowAlpha - 0.3f)),
                    tint = rarityColor.copy(alpha = 0.3f)
                )
            }
            
            Column(
                modifier = Modifier.padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎉 ACHIEVEMENT UNLOCKED! 🎉",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = rarityColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Icon(
                    imageVector = achievement.icon,
                    contentDescription = achievement.title,
                    modifier = Modifier.size(64.dp),
                    tint = rarityColor
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = achievement.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = achievement.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rarity badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = rarityColor
                    )
                ) {
                    Text(
                        text = achievement.rarity.name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rewards
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Text(
                            text = " +${achievement.rewardXP} XP",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                    }
                    
                    if (achievement.rewardCoins > 0) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFFFFD700)
                            )
                            Text(
                                text = " +${achievement.rewardCoins}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Notification factory functions
object NotificationFactory {
    
    fun createAchievementNotification(achievement: Achievement): GameNotification {
        return GameNotification(
            type = NotificationType.ACHIEVEMENT_UNLOCKED,
            title = "Achievement Unlocked!",
            message = achievement.title,
            icon = achievement.icon,
            color = when (achievement.rarity) {
                AchievementRarity.LEGENDARY -> Color(0xFFFFD700)
                AchievementRarity.EPIC -> Color(0xFF9C27B0)
                AchievementRarity.RARE -> Color(0xFF2196F3)
                AchievementRarity.COMMON -> Color(0xFF4CAF50)
            },
            duration = 4000L,
            xpGained = achievement.rewardXP,
            coinsGained = achievement.rewardCoins
        )
    }
    
    fun createLevelUpNotification(newLevel: Int): GameNotification {
        return GameNotification(
            type = NotificationType.LEVEL_UP,
            title = "Level Up!",
            message = "You've reached Level $newLevel!",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            color = Color(0xFFFFD700),
            duration = 3500L,
            xpGained = 0,
            coinsGained = newLevel * 10 // Bonus coins for leveling up
        )
    }
    
    fun createStreakNotification(streakDays: Int): GameNotification {
        return GameNotification(
            type = NotificationType.STREAK_MILESTONE,
            title = "Streak Milestone!",
            message = "$streakDays days in a row! You're on fire!",
            icon = Icons.Default.LocalFireDepartment,
            color = Color(0xFFFF6B35),
            duration = 3000L,
            xpGained = streakDays * 2,
            coinsGained = streakDays
        )
    }
    
    fun createPerfectScoreNotification(): GameNotification {
        return GameNotification(
            type = NotificationType.PERFECT_SCORE,
            title = "Perfect Score!",
            message = "Flawless performance! 100% correct!",
            icon = Icons.Default.Diamond,
            color = Color(0xFF9C27B0),
            duration = 3000L,
            xpGained = 50,
            coinsGained = 20
        )
    }
    
    fun createXPGainedNotification(xp: Int): GameNotification {
        return GameNotification(
            type = NotificationType.XP_GAINED,
            title = "XP Gained!",
            message = "Great job completing that lesson!",
            icon = Icons.Default.Star,
            color = Color(0xFFFF9800),
            duration = 2000L,
            xpGained = xp
        )
    }
    
    fun createDailyGoalNotification(): GameNotification {
        return GameNotification(
            type = NotificationType.DAILY_GOAL_COMPLETED,
            title = "Daily Goal Complete!",
            message = "You've reached your daily learning goal!",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF4CAF50),
            duration = 3000L,
            xpGained = 100,
            coinsGained = 25
        )
    }
}
