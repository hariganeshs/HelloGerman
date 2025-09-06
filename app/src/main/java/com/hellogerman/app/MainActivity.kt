package com.hellogerman.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hellogerman.app.ui.navigation.Screen
import com.hellogerman.app.ui.screens.*
import com.hellogerman.app.ui.theme.HelloGermanTheme
import com.hellogerman.app.ui.theme.ResponsiveTheme
import com.hellogerman.app.ui.utils.ResponsiveUtils
import com.hellogerman.app.ui.utils.ResponsiveLayout
import com.hellogerman.app.ui.utils.ResponsiveNavigationLayout
import com.hellogerman.app.ui.components.ResponsiveNavigation
import com.hellogerman.app.ui.viewmodel.MainViewModel
import com.hellogerman.app.ui.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            
            HelloGermanTheme(darkTheme = isDarkMode) {
                ResponsiveTheme(darkTheme = isDarkMode) {
                    HelloGermanApp()
                }
            }
        }
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle configuration changes (orientation, screen size, etc.)
        // The UI will automatically recompose due to configChanges in manifest
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelloGermanApp() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    
    val userProgress by mainViewModel.userProgress.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()
    
    if (isLoading) {
        SplashScreen(navController)
    } else {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        ResponsiveNavigationLayout(
            bottomBar = {
                if (currentDestination?.route !in listOf(Screen.Splash.route, Screen.Onboarding.route)) {
                    ResponsiveNavigation(
                        navController = navController,
                        currentDestination = currentDestination
                    )
                }
            }
        ) {
            ResponsiveLayout {
                NavHost(
                    navController = navController,
                    startDestination = if (userProgress?.isOnboarded == true) Screen.Dashboard.route else Screen.Onboarding.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                composable(
                    route = Screen.Splash.route,
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) }
                ) {
                    SplashScreen(navController)
                }
                
                composable(
                    route = Screen.Onboarding.route,
                    enterTransition = { 
                        slideInHorizontally(
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) { it } + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = { 
                        slideOutHorizontally(
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) { -it / 3 } + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    OnboardingScreen(navController, mainViewModel)
                }
                
                composable(
                    route = Screen.Dashboard.route,
                    enterTransition = { 
                        slideInVertically(
                            animationSpec = tween(500, easing = FastOutSlowInEasing)
                        ) { it / 2 } + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = { 
                        slideOutVertically(
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) { -it / 2 } + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    DashboardScreen(navController, mainViewModel)
                }
                
                composable(
                    route = Screen.Lesen.route,
                    enterTransition = { 
                        slideInHorizontally(
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) { it / 2 } + fadeIn(animationSpec = tween(300, delayMillis = 100))
                    },
                    exitTransition = { 
                        slideOutHorizontally(
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) { -it / 3 } + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    LesenScreen(navController, mainViewModel)
                }
                
                composable(
                    route = Screen.Hoeren.route,
                    enterTransition = { 
                        slideInHorizontally(
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) { it / 2 } + fadeIn(animationSpec = tween(300, delayMillis = 100))
                    },
                    exitTransition = { 
                        slideOutHorizontally(
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) { -it / 3 } + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    HoerenScreen(navController, mainViewModel)
                }
                
                composable(
                    route = Screen.Schreiben.route,
                    enterTransition = { 
                        slideInHorizontally(
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) { it / 2 } + fadeIn(animationSpec = tween(300, delayMillis = 100))
                    },
                    exitTransition = { 
                        slideOutHorizontally(
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) { -it / 3 } + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    SchreibenScreen(navController, mainViewModel)
                }
                
                composable(
                    route = Screen.Sprechen.route,
                    enterTransition = { 
                        slideInHorizontally(
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) { it / 2 } + fadeIn(animationSpec = tween(300, delayMillis = 100))
                    },
                    exitTransition = { 
                        slideOutHorizontally(
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) { -it / 3 } + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    SprechenScreen(navController, mainViewModel)
                }
                
                composable(Screen.Settings.route) {
                    SettingsScreen(navController)
                }
                
                composable(Screen.Progress.route) {
                    ProgressScreen(navController, mainViewModel)
                }
                
                composable("lesson/{lessonId}") { backStackEntry ->
                    val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: 0
                    LessonDetailScreen(navController, lessonId)
                }
                
                composable("hoeren_lesson/{lessonId}") { backStackEntry ->
                    val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: 0
                    HoerenLessonDetailScreen(navController, lessonId)
                }
                
                composable("schreiben_lesson/{lessonId}") { backStackEntry ->
                    val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: 0
                    SchreibenLessonDetailScreen(navController, lessonId)
                }
                
                composable("sprechen_lesson/{lessonId}") { backStackEntry ->
                    val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: 0
                    SprechenLessonDetailScreen(navController, lessonId)
                }

                // Grammar routes
                composable(Screen.Grammar.route) {
                    GrammarDashboard(navController)
                }
                composable(Screen.GrammarTopicList.route) { backStackEntry ->
                    val level = backStackEntry.arguments?.getString("level") ?: "A1"
                    GrammarTopicListScreen(navController, level)
                }
                composable(Screen.GrammarLesson.route) { backStackEntry ->
                    val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: 0
                    GrammarLessonScreen(navController, lessonId)
                }
                composable(Screen.GrammarQuiz.route) { backStackEntry ->
                    val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: 0
                    GrammarQuizScreen(navController, lessonId)
                }
                
                composable(
                    route = Screen.Dictionary.route,
                    enterTransition = { 
                        slideInHorizontally(
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) { it / 2 } + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = { 
                        slideOutHorizontally(
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) { -it / 3 } + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    DictionaryScreen(navController)
                }
                
                composable(
                    route = Screen.Gamification.route,
                    enterTransition = { 
                        slideInVertically(
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) { it / 2 } + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = { 
                        slideOutVertically(
                            animationSpec = tween(300, easing = FastOutLinearInEasing)
                        ) { -it / 2 } + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    GamificationScreen(navController)
                }
            }
        }
    }
}
}
