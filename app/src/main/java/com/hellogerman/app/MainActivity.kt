package com.hellogerman.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
import com.hellogerman.app.ui.viewmodel.MainViewModel
import com.hellogerman.app.ui.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            
            HelloGermanTheme(darkTheme = isDarkMode) {
                HelloGermanApp()
            }
        }
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
        
        Scaffold(
            bottomBar = {
                if (currentDestination?.route !in listOf(Screen.Splash.route, Screen.Onboarding.route)) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        val navItems = listOf(
                            NavigationItem(
                                route = Screen.Dashboard.route,
                                icon = Icons.Default.Home,
                                label = "Home"
                            ),
                            NavigationItem(
                                route = Screen.Dictionary.route,
                                icon = Icons.Default.Translate,
                                label = "Dict"
                            ),
                            NavigationItem(
                                route = Screen.Lesen.route,
                                icon = Icons.Default.List,
                                label = "Read"
                            ),
                            NavigationItem(
                                route = Screen.Hoeren.route,
                                icon = Icons.Default.PlayArrow,
                                label = "Listen"
                            ),
                            NavigationItem(
                                route = Screen.Schreiben.route,
                                icon = Icons.Default.Edit,
                                label = "Write"
                            ),
                            NavigationItem(
                                route = Screen.Grammar.route,
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                label = "Grammar"
                            )
                        )
                        
                        navItems.forEach { item ->
                            NavigationBarItem(
                                icon = { 
                                    Icon(
                                        imageVector = item.icon, 
                                        contentDescription = item.label,
                                        modifier = Modifier.size(22.dp)
                                    ) 
                                },
                                label = { 
                                    Text(
                                        text = item.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    ) 
                                },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                onClick = {
                                    navController.navigate(item.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (userProgress?.isOnboarded == true) Screen.Dashboard.route else Screen.Onboarding.route,
                modifier = Modifier.padding(innerPadding)
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

data class NavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)