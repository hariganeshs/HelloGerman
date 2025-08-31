package com.hellogerman.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.theme.LesenColor
import com.hellogerman.app.ui.viewmodel.LessonViewModel
import com.hellogerman.app.ui.viewmodel.MainViewModel
import com.hellogerman.app.ui.components.LevelSelector
import com.hellogerman.app.ui.components.ProgressSummary
import com.hellogerman.app.ui.components.EnhancedCard
import com.hellogerman.app.ui.components.EnhancedCardStyle
import com.hellogerman.app.ui.components.LessonIllustration
import com.hellogerman.app.ui.components.CharacterDisplay
import com.hellogerman.app.ui.animations.enhancedPressAnimation
import com.hellogerman.app.ui.animations.entranceAnimation
import com.hellogerman.app.ui.animations.floatingAnimation
import com.hellogerman.app.ads.BannerAd2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LesenScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    val lessonViewModel: LessonViewModel = viewModel()
    val userProgress by mainViewModel.userProgress.collectAsState()
    val lessons by lessonViewModel.lessons.collectAsState()
    val isLoading by lessonViewModel.isLoading.collectAsState()

    val currentLevel = userProgress?.currentLevel ?: "A1"

    var completedCount by remember { mutableStateOf(0) }
    var totalCount by remember { mutableStateOf(0) }
    var averageScore by remember { mutableStateOf(0.0) }

    // Debug: Log lessons count and details
    LaunchedEffect(lessons) {
        android.util.Log.d("LesenScreen", "Received ${lessons.size} lessons for lesen/$currentLevel")
        lessons.forEachIndexed { index, lesson ->
            android.util.Log.d("LesenScreen", "Lesson $index: ${lesson.title} (ID: ${lesson.id}, Order: ${lesson.orderIndex})")
        }
    }

    LaunchedEffect(currentLevel) {
        lessonViewModel.loadLessons("lesen", currentLevel)
        completedCount = lessonViewModel.getCompletedLessonsCount("lesen", currentLevel)
        totalCount = lessonViewModel.getTotalLessonsCount("lesen", currentLevel)
        averageScore = lessonViewModel.getAverageScore("lesen", currentLevel)

        // Debug logging
        android.util.Log.d("LesenScreen", "Loading lessons for level: $currentLevel")
        android.util.Log.d("LesenScreen", "Completed: $completedCount, Total: $totalCount")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lesen",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LesenColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Level selector
                    LevelSelector(
                        currentLevel = currentLevel,
                        onLevelSelected = { level ->
                            mainViewModel.updateCurrentLevel(level)
                        },
                        accentColor = LesenColor
                    )
                }
                
                item {
                    // Progress summary
                    ProgressSummary(
                        completed = completedCount,
                        total = totalCount,
                        averageScore = averageScore,
                        accentColor = LesenColor
                    )
                }
                
                items(lessons) { lesson ->
                    LessonCard(
                        lesson = lesson,
                        onClick = {
                            // Navigate to lesson detail
                            navController.navigate("lesson/${lesson.id}")
                        }
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
}



@Composable
fun LessonCard(
    lesson: com.hellogerman.app.data.entities.Lesson,
    onClick: () -> Unit
) {
    EnhancedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (lesson.isCompleted) Modifier.floatingAnimation() else Modifier),
        onClick = onClick,
        style = if (lesson.isCompleted) EnhancedCardStyle.Success else EnhancedCardStyle.Default
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (lesson.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = LesenColor,
                        modifier = Modifier.size(32.dp)
                    )
                                        } else {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Lesson",
                                tint = LesenColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lesson.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = lesson.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (lesson.isCompleted) {
                            Text(
                                text = "Score: ${lesson.score}%",
                                fontSize = 12.sp,
                                color = LesenColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Lesson illustration
                    LessonIllustration(
                        illustrationResId = lesson.illustrationResId,
                        modifier = Modifier.padding(start = 8.dp),
                        contentDescription = "Illustration for ${lesson.title}"
                    )
                }

                // Character display for completed lessons
                if (lesson.isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        CharacterDisplay(
                            characterResId = lesson.characterResId,
                            animationType = lesson.animationType,
                            contentDescription = "Success character for ${lesson.title}"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Great job! ✓",
                            fontSize = 12.sp,
                            color = LesenColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
                                Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
        }
    }
}
