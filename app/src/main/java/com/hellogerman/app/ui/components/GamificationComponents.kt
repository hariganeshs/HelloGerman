package com.hellogerman.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.gamification.*
import com.hellogerman.app.ui.theme.*
import com.hellogerman.app.ui.viewmodel.MainViewModel

@Composable
fun GamificationStatsSection(mainViewModel: MainViewModel, navController: NavController) {
    val userProgress by mainViewModel.userProgress.collectAsState()
    val grammarPoints by mainViewModel.grammarTotalPoints.collectAsState()
    
    val totalXP = calculateTotalXP(userProgress, grammarPoints)
    val currentLevel = RewardSystem.calculateLevel(totalXP)
    val coins = calculateCoins(userProgress, grammarPoints)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Level $currentLevel",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = RewardSystem.getLevelTitle(currentLevel),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coins",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MiniStatCard(
                    title = "Streak",
                    value = "${userProgress?.currentStreak ?: 0}",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFFF6B35),
                    modifier = Modifier.weight(1f)
                )
                MiniStatCard(
                    title = "XP",
                    value = "$totalXP",
                    icon = Icons.Default.Star,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.weight(1f)
                )
                MiniStatCard(
                    title = "Lessons",
                    value = "${userProgress?.totalLessonsCompleted ?: 0}",
                    icon = Icons.Default.School,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MiniStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun DailyChallengeSection(navController: NavController) {
    val challenges = RewardSystem.generateDailyChallenges().take(1) // Show only one challenge
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Challenge",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(
                onClick = { navController.navigate("gamification") }
            ) {
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        challenges.forEach { challenge ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("gamification") },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = challenge.icon,
                        contentDescription = challenge.title,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = challenge.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = challenge.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Progress bar
                        LinearProgressIndicator(
                            progress = { (challenge.progress / challenge.maxProgress.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "XP",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFF9800)
                            )
                            Text(
                                text = " +${challenge.rewardXP}",
                                fontSize = 12.sp,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "${challenge.progress}/${challenge.maxProgress}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementPreviewSection(mainViewModel: MainViewModel, navController: NavController) {
    val userProgress by mainViewModel.userProgress.collectAsState()
    val grammarPoints by mainViewModel.grammarTotalPoints.collectAsState()
    
    val achievements = AchievementManager.getAllAchievements()
    val unlockedAchievements = AchievementManager.checkAchievements(userProgress, grammarPoints)
    val recentAchievements = achievements.take(3) // Show first 3 for preview
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Achievements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(
                onClick = { navController.navigate("gamification") }
            ) {
                Text("View All (${unlockedAchievements.size}/${achievements.size})")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            recentAchievements.forEach { achievement ->
                val isUnlocked = unlockedAchievements.any { it.id == achievement.id }
                
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate("gamification") },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) {
                            when (achievement.rarity) {
                                AchievementRarity.LEGENDARY -> Color(0xFFFFD700).copy(alpha = 0.1f)
                                AchievementRarity.EPIC -> Color(0xFF9C27B0).copy(alpha = 0.1f)
                                AchievementRarity.RARE -> Color(0xFF2196F3).copy(alpha = 0.1f)
                                AchievementRarity.COMMON -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            }
                        } else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isUnlocked) 6.dp else 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val iconColor = if (isUnlocked) {
                            when (achievement.rarity) {
                                AchievementRarity.LEGENDARY -> Color(0xFFFFD700)
                                AchievementRarity.EPIC -> Color(0xFF9C27B0)
                                AchievementRarity.RARE -> Color(0xFF2196F3)
                                AchievementRarity.COMMON -> Color(0xFF4CAF50)
                            }
                        } else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        
                        Icon(
                            imageVector = achievement.icon,
                            contentDescription = achievement.title,
                            modifier = Modifier.size(28.dp),
                            tint = iconColor
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = achievement.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface
                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                        
                        if (isUnlocked) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                modifier = Modifier.size(16.dp),
                                tint = iconColor
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper functions for XP and coins calculation
fun calculateTotalXP(userProgress: com.hellogerman.app.data.entities.UserProgress?, grammarPoints: Int): Int {
    if (userProgress == null) return 0
    
    return (userProgress.totalLessonsCompleted * 25) + // 25 XP per lesson
           (userProgress.currentStreak * 10) + // 10 XP per day streak
           (grammarPoints / 10) + // Grammar points to XP conversion
           (userProgress.lesenScore * 5) + // Skill scores to XP
           (userProgress.hoerenScore * 5) +
           (userProgress.schreibenScore * 5) +
           (userProgress.sprechenScore * 5)
}

fun calculateCoins(userProgress: com.hellogerman.app.data.entities.UserProgress?, grammarPoints: Int): Int {
    if (userProgress == null) return 0
    
    return (userProgress.totalLessonsCompleted * 5) + // 5 coins per lesson
           (userProgress.currentStreak * 2) + // 2 coins per day streak
           (grammarPoints / 50) // Grammar points to coins conversion
}
