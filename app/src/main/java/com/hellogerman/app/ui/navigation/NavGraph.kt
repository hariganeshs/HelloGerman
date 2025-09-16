package com.hellogerman.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hellogerman.app.ui.screens.*

open class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object Lesen : Screen("lesen")
    object Hoeren : Screen("hoeren")
    object Schreiben : Screen("schreiben")
    object Sprechen : Screen("sprechen")
    object Grammar : Screen("grammar")
    object GrammarTopicList : Screen("grammar_topics/{level}") {
        fun createRoute(level: String) = "grammar_topics/$level"
    }
    object GrammarLesson : Screen("grammar_lesson/{lessonId}") {
        fun createRoute(lessonId: Int) = "grammar_lesson/$lessonId"
    }
    object GrammarQuiz : Screen("grammar_quiz/{lessonId}") {
        fun createRoute(lessonId: Int) = "grammar_quiz/$lessonId"
    }
    object LessonDetail : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: Int) = "lesson/$lessonId"
    }
    object Quiz : Screen("quiz/{lessonId}") {
        fun createRoute(lessonId: Int) = "quiz/$lessonId"
    }
    object Settings : Screen("settings")
    object Onboarding : Screen("onboarding")
    object Tutorial : Screen("tutorial")
    object Progress : Screen("progress")
    object Dictionary : Screen("dictionary")
    object DictionaryWithWord : Screen("dictionary/{word}") {
        fun createRoute(word: String) = "dictionary/$word"
    }
    object Vocabulary : Screen("vocabulary")
    object Gamification : Screen("gamification")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        
        composable(Screen.Lesen.route) {
            LesenScreen(navController)
        }
        
        composable(Screen.Hoeren.route) {
            HoerenScreen(navController)
        }
        
        composable(Screen.Schreiben.route) {
            SchreibenScreen(navController)
        }
        
        composable(Screen.Sprechen.route) {
            SprechenScreen(navController)
        }
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
        
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        
        composable(Screen.Progress.route) {
            ProgressScreen(navController)
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }

        composable(Screen.Tutorial.route) {
            TutorialScreen(navController)
        }
        
        composable(Screen.Dictionary.route) {
            DictionaryScreen(navController)
        }

        composable(Screen.DictionaryWithWord.route) { backStackEntry ->
            val word = backStackEntry.arguments?.getString("word") ?: ""
            DictionaryScreen(navController, initialWord = word)
        }
        
        composable(Screen.Gamification.route) {
            GamificationScreen(navController)
        }
    }
}
