package com.hellogerman.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamificationScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel(),
    themeViewModel: com.hellogerman.app.ui.viewmodel.ThemeViewModel = viewModel()
) {
    val userProgress by mainViewModel.userProgress.collectAsState()
    val grammarPoints by mainViewModel.grammarTotalPoints.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Achievements", "Daily", "Rewards")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Achievements & Rewards",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // User Level and XP Section
            UserLevelCard(
                totalXP = calculateTotalXP(userProgress, grammarPoints),
                coins = calculateCoins(userProgress, grammarPoints),
                modifier = Modifier.padding(16.dp)
            )
            
            // Tab Row (Scrollable to avoid multiline labels)
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                maxLines = 1,
                                softWrap = false,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            
            // Content based on selected tab
            when (selectedTab) {
                0 -> AchievementsTab(userProgress, grammarPoints)
                1 -> DailyChallengesTab()
                2 -> RewardsTab(mainViewModel, themeViewModel)
            }
        }
    }
}

@Composable
private fun UserLevelCard(
    totalXP: Int,
    coins: Int,
    modifier: Modifier = Modifier
) {
    val currentLevel = RewardSystem.calculateLevel(totalXP)
    val nextLevelXP = RewardSystem.getXPForNextLevel(currentLevel)
    val levelProgress = if (currentLevel == 1) {
        totalXP / nextLevelXP.toFloat()
    } else {
        val currentLevelXP = RewardSystem.getXPForNextLevel(currentLevel - 1)
        (totalXP - currentLevelXP) / (nextLevelXP - currentLevelXP).toFloat()
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = RewardSystem.getLevelTitle(currentLevel),
                        fontSize = 16.sp,
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
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coins",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
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
                        text = "$totalXP XP",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$nextLevelXP XP",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(levelProgress.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4CAF50),
                                        Color(0xFF8BC34A)
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementsTab(userProgress: com.hellogerman.app.data.entities.UserProgress?, grammarPoints: Int) {
    val achievements = AchievementManager.getAllAchievements()
    val unlockedAchievements = AchievementManager.checkAchievements(userProgress, grammarPoints)
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Your Achievements",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${unlockedAchievements.size}/${achievements.size} unlocked",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(achievements) { achievement ->
            AchievementCard(
                achievement = achievement,
                isUnlocked = unlockedAchievements.any { it.id == achievement.id },
                userProgress = userProgress,
                grammarPoints = grammarPoints
            )
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    isUnlocked: Boolean,
    userProgress: com.hellogerman.app.data.entities.UserProgress?,
    grammarPoints: Int
) {
    var showCelebration by remember { mutableStateOf(false) }
    
    val cardColor = when {
        isUnlocked -> when (achievement.rarity) {
            AchievementRarity.LEGENDARY -> Color(0xFFFFD700).copy(alpha = 0.1f)
            AchievementRarity.EPIC -> Color(0xFF9C27B0).copy(alpha = 0.1f)
            AchievementRarity.RARE -> Color(0xFF2196F3).copy(alpha = 0.1f)
            AchievementRarity.COMMON -> Color(0xFF4CAF50).copy(alpha = 0.1f)
        }
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        isUnlocked -> when (achievement.rarity) {
            AchievementRarity.LEGENDARY -> Color(0xFFFFD700)
            AchievementRarity.EPIC -> Color(0xFF9C27B0)
            AchievementRarity.RARE -> Color(0xFF2196F3)
            AchievementRarity.COMMON -> Color(0xFF4CAF50)
        }
        else -> Color.Transparent
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isUnlocked) Modifier.border(
                    2.dp,
                    borderColor,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .clickable {
                if (isUnlocked) showCelebration = true
            },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) borderColor.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = achievement.icon,
                    contentDescription = achievement.title,
                    modifier = Modifier.size(32.dp),
                    tint = if (isUnlocked) borderColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = achievement.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (isUnlocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            modifier = Modifier.size(16.dp),
                            tint = borderColor
                        )
                    }
                }
                
                Text(
                    text = achievement.description,
                    fontSize = 14.sp,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                
                if (achievement.maxProgress > 1) {
                    val currentProgress = getCurrentProgress(achievement, userProgress, grammarPoints)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$currentProgress/${achievement.maxProgress}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LinearProgressIndicator(
                            progress = { (currentProgress / achievement.maxProgress.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .height(4.dp)
                                .weight(1f),
                            color = if (isUnlocked) borderColor else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                
                if (isUnlocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Text(
                            text = " +${achievement.rewardXP} XP",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                        if (achievement.rewardCoins > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFD700)
                            )
                            Text(
                                text = " +${achievement.rewardCoins}",
                                fontSize = 12.sp,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Celebration Animation
    if (showCelebration) {
        LaunchedEffect(showCelebration) {
            // Auto-hide after 2 seconds
            kotlinx.coroutines.delay(2000)
            showCelebration = false
        }
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Add celebration effects here
        }
    }
}

@Composable
private fun DailyChallengesTab() {
    val challenges = RewardSystem.generateDailyChallenges()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Daily Challenges",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Complete challenges to earn extra rewards!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(challenges) { challenge ->
            DailyChallengeCard(challenge)
        }
    }
}

@Composable
private fun DailyChallengeCard(challenge: DailyChallenge) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (challenge.isCompleted) 
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = challenge.icon,
                    contentDescription = challenge.title,
                    modifier = Modifier.size(32.dp),
                    tint = if (challenge.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = challenge.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (challenge.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${challenge.progress}/${challenge.maxProgress}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { (challenge.progress / challenge.maxProgress.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .height(6.dp)
                        .weight(1f),
                    color = if (challenge.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rewards
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "XP",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFF9800)
                )
                Text(
                    text = " +${challenge.rewardXP} XP",
                    fontSize = 12.sp,
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Coins",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFFD700)
                )
                Text(
                    text = " +${challenge.rewardCoins}",
                    fontSize = 12.sp,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun RewardsTab(mainViewModel: MainViewModel, themeViewModel: com.hellogerman.app.ui.viewmodel.ThemeViewModel) {
    val rewards = RewardSystem.getAllRewards()
    val groupedRewards = rewards.groupBy { it.category }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Reward Shop",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Spend your coins on amazing rewards!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        groupedRewards.forEach { (category, categoryRewards) ->
            item {
                Text(
                    text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(categoryRewards) { reward ->
                RewardCard(reward, mainViewModel, themeViewModel)
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    mainViewModel: MainViewModel,
    themeViewModel: com.hellogerman.app.ui.viewmodel.ThemeViewModel
) {
    val userProgress by mainViewModel.userProgress.collectAsState()
    val userCoins = userProgress?.coins ?: 0
    val canAfford = userCoins >= reward.cost
    val isUnlocked = reward.isUnlocked

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = reward.icon,
                contentDescription = reward.title,
                modifier = Modifier.size(40.dp),
                tint = when (reward.rarity) {
                    RewardRarity.LEGENDARY -> Color(0xFFFFD700)
                    RewardRarity.EPIC -> Color(0xFF9C27B0)
                    RewardRarity.RARE -> Color(0xFF2196F3)
                    RewardRarity.COMMON -> MaterialTheme.colorScheme.primary
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = reward.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Cost",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFFFD700)
                    )
                    Text(
                        text = " ${reward.cost}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = {
                        if (canAfford && !isUnlocked) {
                            // Handle theme purchase
                            val themeId = when (reward.id) {
                                "ocean_theme" -> "ocean"
                                "forest_theme" -> "forest"
                                "sunset_theme" -> "sunset"
                                "mountain_theme" -> "mountain"
                                "desert_theme" -> "desert"
                                "space_theme" -> "space"
                                "retro_theme" -> "retro"
                                "minimalist_theme" -> "minimalist"
                                "autumn_theme" -> "autumn"
                                "winter_theme" -> "winter"
                                else -> "default"
                            }

                            // Apply theme
                            themeViewModel.setSelectedTheme(themeId)

                            // Deduct coins
                            userProgress?.let { progress ->
                                val updatedProgress = progress.copy(
                                    coins = progress.coins - reward.cost
                                )
                                // Update progress through repository
                                // This would need to be implemented in the repository
                            }
                        }
                    },
                    enabled = canAfford && !isUnlocked,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(if (isUnlocked) "Owned" else if (canAfford) "Buy" else "Can't Afford")
                }
            }
        }
    }
}




// Helper functions
private fun getCurrentProgress(
    achievement: Achievement, 
    userProgress: com.hellogerman.app.data.entities.UserProgress?, 
    grammarPoints: Int
): Int {
    return when (achievement.id) {
        "week_warrior", "unstoppable", "legend" -> userProgress?.currentStreak ?: 0
        "dedicated_learner", "scholar" -> userProgress?.totalLessonsCompleted ?: 0
        "grammar_rookie", "grammar_master" -> grammarPoints
        "reading_pro" -> userProgress?.lesenScore ?: 0
        "listening_expert" -> userProgress?.hoerenScore ?: 0
        "writing_wizard" -> userProgress?.schreibenScore ?: 0
        "speaking_champion" -> userProgress?.sprechenScore ?: 0
        "polyglot" -> minOf(
            userProgress?.lesenScore ?: 0,
            userProgress?.hoerenScore ?: 0,
            userProgress?.schreibenScore ?: 0,
            userProgress?.sprechenScore ?: 0
        )
        else -> 0
    }
}

private fun calculateTotalXP(userProgress: com.hellogerman.app.data.entities.UserProgress?, grammarPoints: Int): Int {
    if (userProgress == null) return 0
    
    return (userProgress.totalLessonsCompleted * 25) + // 25 XP per lesson
           (userProgress.currentStreak * 10) + // 10 XP per day streak
           (grammarPoints / 10) + // Grammar points to XP conversion
           (userProgress.lesenScore * 5) + // Skill scores to XP
           (userProgress.hoerenScore * 5) +
           (userProgress.schreibenScore * 5) +
           (userProgress.sprechenScore * 5)
}

private fun calculateCoins(userProgress: com.hellogerman.app.data.entities.UserProgress?, grammarPoints: Int): Int {
    if (userProgress == null) return 0
    
    return (userProgress.totalLessonsCompleted * 5) + // 5 coins per lesson
           (userProgress.currentStreak * 2) + // 2 coins per day streak
           (grammarPoints / 50) // Grammar points to coins conversion
}
