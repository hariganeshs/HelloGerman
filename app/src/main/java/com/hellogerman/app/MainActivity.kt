package com.hellogerman.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloGermanTheme {
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
                    NavigationBar {
                        val navItems = listOf(
                            NavigationItem(
                                route = Screen.Dashboard.route,
                                icon = Icons.Default.Home,
                                label = "Dashboard"
                            ),
                            NavigationItem(
                                route = Screen.Lesen.route,
                                icon = Icons.Default.List,
                                label = "Lesen"
                            ),
                            NavigationItem(
                                route = Screen.Hoeren.route,
                                icon = Icons.Default.PlayArrow,
                                label = "Hören"
                            ),
                            NavigationItem(
                                route = Screen.Schreiben.route,
                                icon = Icons.Default.Edit,
                                label = "Schreiben"
                            ),
                            NavigationItem(
                                route = Screen.Sprechen.route,
                                icon = Icons.Default.MoreVert,
                                label = "Sprechen"
                            ),
                            NavigationItem(
                                route = Screen.Grammar.route,
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                label = "Grammar"
                            )
                        )
                        
                        navItems.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
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
                                }
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
                composable(Screen.Splash.route) {
                    SplashScreen(navController)
                }
                
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(navController, mainViewModel)
                }
                
                composable(Screen.Dashboard.route) {
                    DashboardScreen(navController, mainViewModel)
                }
                
                composable(Screen.Lesen.route) {
                    LesenScreen(navController, mainViewModel)
                }
                
                composable(Screen.Hoeren.route) {
                    HoerenScreen(navController, mainViewModel)
                }
                
                composable(Screen.Schreiben.route) {
                    SchreibenScreen(navController, mainViewModel)
                }
                
                composable(Screen.Sprechen.route) {
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
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)