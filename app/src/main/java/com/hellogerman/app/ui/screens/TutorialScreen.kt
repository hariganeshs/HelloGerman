package com.hellogerman.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.theme.*
import com.hellogerman.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    var currentStep by remember { mutableStateOf(0) }
    var showSkipDialog by remember { mutableStateOf(false) }

    val tutorialSteps = listOf(
        TutorialStep(
            title = "Welcome to HelloGerman! 🇩🇪",
            description = "Your journey to learning German starts here. This app will help you master reading, listening, writing, and speaking skills through interactive lessons and gamification.",
            icon = Icons.Default.EmojiEvents,
            backgroundColor = MaterialTheme.colorScheme.primary
        ),
        TutorialStep(
            title = "Explore Your Dashboard",
            description = "Your main hub shows your progress, daily challenges, and quick access to all features. Track your XP, coins, and achievements here.",
            icon = Icons.Default.Dashboard,
            backgroundColor = LesenColor
        ),
        TutorialStep(
            title = "Master German Skills",
            description = "Learn through 5 core skills:\n\n📖 Lesen (Reading)\n🎧 Hören (Listening)\n✍️ Schreiben (Writing)\n🗣️ Sprechen (Speaking)\n📚 Grammar",
            icon = Icons.Default.Book,
            backgroundColor = HoerenColor
        ),
        TutorialStep(
            title = "Earn XP & Achievements",
            description = "Complete lessons to earn XP and unlock achievements. Maintain daily streaks for bonus rewards and special badges!",
            icon = Icons.Default.Star,
            backgroundColor = SchreibenColor
        ),
        TutorialStep(
            title = "Use the Dictionary",
            description = "Long-press any German word in lesson content to look it up instantly. Or tap vocabulary words to explore their meanings, examples, and pronunciations.",
            icon = Icons.Default.Translate,
            backgroundColor = SprechenColor
        ),
        TutorialStep(
            title = "Customize Your Experience",
            description = "Unlock beautiful themes with coins earned from lessons. Choose from Ocean, Forest, Sunset, and more premium themes!",
            icon = Icons.Default.Palette,
            backgroundColor = GrammarColor
        ),
        TutorialStep(
            title = "Track Your Progress",
            description = "Monitor your improvement with detailed analytics. See your skill scores, completed lessons, and learning streaks.",
            icon = Icons.Default.Analytics,
            backgroundColor = MaterialTheme.colorScheme.tertiary
        ),
        TutorialStep(
            title = "Ready to Start Learning?",
            description = "You're all set! Begin with your first lesson or explore the app. Remember, consistency is key to language learning success.",
            icon = Icons.Default.PlayArrow,
            backgroundColor = MaterialTheme.colorScheme.secondary
        )
    )

    // Auto-advance animation
    LaunchedEffect(currentStep) {
        if (currentStep < tutorialSteps.size - 1) {
            delay(4000) // Auto-advance after 4 seconds
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tutorial",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(
                        onClick = { showSkipDialog = true }
                    ) {
                        Text("Skip")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                tutorialSteps.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentStep)
                                    MaterialTheme.colorScheme.primary
                                else if (index < currentStep)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                    )
                    if (index < tutorialSteps.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // Step counter
            Text(
                text = "${currentStep + 1} of ${tutorialSteps.size}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Main content card
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) { it } + fadeIn() togetherWith slideOutHorizontally(
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    ) { -it } + fadeOut()
                }
            ) { stepIndex ->
                val step = tutorialSteps[stepIndex]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon with animated background
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            step.backgroundColor.copy(alpha = 0.2f),
                                            step.backgroundColor.copy(alpha = 0.1f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = step.title,
                                tint = step.backgroundColor,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Title
                        Text(
                            text = step.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description
                        Text(
                            text = step.description,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Previous")
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < tutorialSteps.size - 1) {
                            currentStep++
                        } else {
                            // Tutorial completed
                            mainViewModel.markTutorialCompleted()
                            navController.navigate("dashboard") {
                                popUpTo("tutorial") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (currentStep < tutorialSteps.size - 1) "Next" else "Start Learning!")
                }
            }
        }
    }

    // Skip confirmation dialog
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            title = { Text("Skip Tutorial?") },
            text = { Text("You can always access the tutorial later from Settings. Are you sure you want to skip?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSkipDialog = false
                        mainViewModel.markTutorialCompleted()
                        navController.navigate("dashboard") {
                            popUpTo("tutorial") { inclusive = true }
                        }
                    }
                ) {
                    Text("Skip")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSkipDialog = false }) {
                    Text("Continue Tutorial")
                }
            }
        )
    }
}

data class TutorialStep(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val backgroundColor: Color
)
