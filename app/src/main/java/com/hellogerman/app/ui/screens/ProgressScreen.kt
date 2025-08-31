package com.hellogerman.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.viewmodel.MainViewModel
import com.hellogerman.app.ui.theme.*
import com.hellogerman.app.ads.BannerAd2
import com.hellogerman.app.data.repository.LevelUnlockStatus
import androidx.compose.ui.graphics.Color
import com.hellogerman.app.ui.components.AnimatedProgressBar
import com.hellogerman.app.ui.components.CharacterDisplay
import com.hellogerman.app.data.entities.AnimationType



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    val userProgress by mainViewModel.userProgress.collectAsState()
    val levelUnlockStatus by mainViewModel.levelUnlockStatus.collectAsState()
    val levelCompletionInfo by mainViewModel.levelCompletionInfo.collectAsState()

    var progressText by remember { mutableStateOf("") }

    LaunchedEffect(levelUnlockStatus) {
        levelUnlockStatus?.let {
            progressText = mainViewModel.getLevelProgressText()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Progress Analytics",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header
                Column {
                    Text(
                        text = "Your Learning Journey",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Level ${userProgress?.currentLevel ?: "A1"} • ${userProgress?.totalLessonsCompleted ?: 0} lessons completed",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            item {
                // Overall Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Current Streak",
                        value = "${userProgress?.currentStreak ?: 0}",
                        subtitle = "days",
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f),
                        color = ProgressRed
                    )
                    StatCard(
                        title = "Longest Streak",
                        value = "${userProgress?.longestStreak ?: 0}",
                        subtitle = "days",
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f),
                        color = ProgressGreen
                    )
                }
            }
            
            item {
                // Skill Progress
                Text(
                    text = "Skill Progress",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            item {
                SkillProgressCard(
                    skill = "Lesen (Reading)",
                    score = userProgress?.lesenScore ?: 0,
                    color = LesenColor
                )
            }
            
            item {
                SkillProgressCard(
                    skill = "Hören (Listening)",
                    score = userProgress?.hoerenScore ?: 0,
                    color = HoerenColor
                )
            }
            
            item {
                SkillProgressCard(
                    skill = "Schreiben (Writing)",
                    score = userProgress?.schreibenScore ?: 0,
                    color = SchreibenColor
                )
            }
            
            item {
                SkillProgressCard(
                    skill = "Sprechen (Speaking)",
                    score = userProgress?.sprechenScore ?: 0,
                    color = SprechenColor
                )
            }
            
            item {
                SkillProgressCard(
                    skill = "Grammar",
                    score = userProgress?.grammarScore ?: 0,
                    color = GrammarColor
                )
            }
            
            // Level Unlock Status
            levelUnlockStatus?.let { unlockStatus ->
                item {
                    LevelUnlockCard(unlockStatus = unlockStatus)
                }
            }

            item {
                // Learning Insights
                Text(
                    text = "Learning Insights",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            item {
                InsightsCard(
                    title = "Daily Goal Progress",
                    content = "You've completed ${userProgress?.totalLessonsCompleted ?: 0} lessons. Your daily goal is ${userProgress?.dailyGoal ?: 3} lessons.",
                    icon = Icons.Default.Info,
                    color = AccentBlue
                )
            }
            
            item {
                InsightsCard(
                    title = "Performance Trend",
                    content = "Keep up the great work! Your consistent learning is showing positive results.",
                    icon = Icons.Default.Star,
                    color = ProgressGreen
                )
            }
            
            item {
                InsightsCard(
                    title = "Next Milestone",
                    content = "Complete 5 more lessons to unlock new content and advance your level.",
                    icon = Icons.Default.Info,
                    color = AccentOrange
                )
            }
            
            // Banner Ad
            item {
                BannerAd2(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SkillProgressCard(
    skill: String,
    score: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = skill,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$score%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = score / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun InsightsCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LevelUnlockCard(unlockStatus: LevelUnlockStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (unlockStatus.canUnlock)
                Color(0xFFE8F5E8) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (unlockStatus.canUnlock) "🎉 Level Unlock Ready!" else "Level Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (unlockStatus.canUnlock)
                        Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = if (unlockStatus.canUnlock) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = if (unlockStatus.canUnlock) "Unlocked" else "Locked",
                    tint = if (unlockStatus.canUnlock) Color(0xFF2E7D32) else Color(0xFF757575)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Overall progress
            Text(
                text = "Overall Progress: ${unlockStatus.overallProgress.toInt()}%",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Animated progress bar with character
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedProgressBar(
                    progress = (unlockStatus.overallProgress / 100f).toFloat(),
                    modifier = Modifier.weight(1f),
                    showStar = unlockStatus.canUnlock
                )

                // Character display based on progress
                if (unlockStatus.canUnlock) {
                    CharacterDisplay(
                        characterResId = "ic_success_character",
                        animationType = AnimationType.CONFETTI,
                        contentDescription = "Success celebration character"
                    )
                } else if (unlockStatus.overallProgress > 50) {
                    CharacterDisplay(
                        characterResId = "ic_owl_character",
                        animationType = AnimationType.BOUNCE,
                        contentDescription = "Encouragement character"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${unlockStatus.lessonsCompleted}/${unlockStatus.totalLessons} lessons completed",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (unlockStatus.canUnlock && unlockStatus.nextLevel != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ready to unlock ${unlockStatus.nextLevel}!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                )
            } else if (unlockStatus.nextLevel != null) {
                val remainingPercent = 80 - unlockStatus.overallProgress.toInt()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${remainingPercent}% more to unlock ${unlockStatus.nextLevel}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
