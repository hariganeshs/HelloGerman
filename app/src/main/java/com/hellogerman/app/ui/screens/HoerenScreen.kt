package com.hellogerman.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.theme.HoerenColor
import com.hellogerman.app.ui.viewmodel.MainViewModel
import com.hellogerman.app.ui.viewmodel.LessonViewModel
import com.hellogerman.app.ui.components.LevelSelector
import com.hellogerman.app.ui.components.ProgressSummary
import com.hellogerman.app.data.entities.*
import com.hellogerman.app.ads.BannerAd1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoerenScreen(
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

    LaunchedEffect(currentLevel) {
        lessonViewModel.loadLessons("hoeren", currentLevel)
        completedCount = lessonViewModel.getCompletedLessonsCount("hoeren", currentLevel)
        totalCount = lessonViewModel.getTotalLessonsCount("hoeren", currentLevel)
        averageScore = lessonViewModel.getAverageScore("hoeren", currentLevel)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hören",
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
                CircularProgressIndicator(color = HoerenColor)
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
                        accentColor = HoerenColor
                    )
                }
                
                item {
                    // Progress summary
                    ProgressSummary(
                        completed = completedCount,
                        total = totalCount,
                        averageScore = averageScore,
                        accentColor = HoerenColor
                    )
                }
                
                items(lessons) { lesson ->
                    HoerenLessonCard(
                        lesson = lesson,
                        onClick = {
                            navController.navigate("hoeren_lesson/${lesson.id}")
                        }
                    )
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
    }
}

@Composable
fun HoerenLessonCard(
    lesson: com.hellogerman.app.data.entities.Lesson,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (lesson.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = HoerenColor,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Lesson",
                        tint = HoerenColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                        color = HoerenColor,
                        fontWeight = FontWeight.Medium
                    )
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

