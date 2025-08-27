package com.hellogerman.app.ui.screens

import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    var selectedLevel by remember { mutableStateOf("A1") }
    var selectedLanguage by remember { mutableStateOf("en") }
    
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
    val languages = listOf("en" to "English", "de" to "Deutsch")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Welcome to Hello German",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Let's get started!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Choose your starting level and preferred language",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Level Selection
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What's your German level?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    levels.forEach { level ->
                        FilterChip(
                            onClick = { selectedLevel = level },
                            label = { Text(level) },
                            selected = selectedLevel == level
                        )
                    }
                }
            }
            
            // Language Selection
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose your language",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    languages.forEach { (code, name) ->
                        FilterChip(
                            onClick = { selectedLanguage = code },
                            label = { Text(name) },
                            selected = selectedLanguage == code
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Start Button
            Button(
                onClick = {
                    // Update user progress with onboarding data
                    mainViewModel.updateCurrentLevel(selectedLevel)
                    // TODO: Update language preference
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Start Learning",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
