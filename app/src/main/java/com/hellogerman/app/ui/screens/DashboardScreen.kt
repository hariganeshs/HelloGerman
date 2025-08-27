package com.hellogerman.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.hellogerman.app.ui.theme.*
import com.hellogerman.app.ui.viewmodel.MainViewModel
import com.hellogerman.app.ads.BannerAd1
import com.hellogerman.app.data.DatabaseInitializer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    val userProgress by mainViewModel.userProgress.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Column {
                Text(
                    text = "Willkommen!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Level ${userProgress?.currentLevel ?: "A1"}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        item {
            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Streak",
                    value = "${userProgress?.currentStreak ?: 0}",
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f),
                    color = ProgressRed
                )
                StatCard(
                    title = "Lessons",
                    value = "${userProgress?.totalLessonsCompleted ?: 0}",
                    icon = Icons.Default.List,
                    modifier = Modifier.weight(1f),
                    color = ProgressGreen
                )
            }
        }
        
        item {
            // Skills Progress
            Text(
                text = "Your Progress",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            SkillsProgressSection(
                userProgress = userProgress,
                onSkillClick = { skill ->
                    navController.navigate(skill)
                }
            )
        }
        
        item {
            // Quick Actions
            Text(
                text = "Quick Actions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            QuickActionsSection(navController)
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

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
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
        }
    }
}

@Composable
fun SkillsProgressSection(
    userProgress: com.hellogerman.app.data.entities.UserProgress?,
    onSkillClick: (String) -> Unit
) {
    val skills = listOf(
        SkillProgress("Lesen", "lesen", LesenColor, userProgress?.lesenScore ?: 0),
        SkillProgress("Hören", "hoeren", HoerenColor, userProgress?.hoerenScore ?: 0),
        SkillProgress("Schreiben", "schreiben", SchreibenColor, userProgress?.schreibenScore ?: 0),
        SkillProgress("Sprechen", "sprechen", SprechenColor, userProgress?.sprechenScore ?: 0)
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        skills.forEach { skill ->
            SkillProgressCard(
                skill = skill,
                onClick = { onSkillClick(skill.route) }
            )
        }
    }
}

@Composable
fun SkillProgressCard(
    skill: SkillProgress,
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
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(skill.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = skill.name.first().toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = skill.color
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = skill.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (skill.score / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = skill.color,
                    trackColor = skill.color.copy(alpha = 0.2f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "${skill.score}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = skill.color
            )
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    val quickActions = listOf(
        QuickAction("Progress Analytics", Icons.Default.Info, "progress"),
        QuickAction("Practice Quiz", Icons.Default.PlayArrow, "lesen"),
        QuickAction("Settings", Icons.Default.Settings, "settings")
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        quickActions.forEach { action ->
            QuickActionCard(
                action = action,
                onClick = { navController.navigate(action.route) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

data class SkillProgress(
    val name: String,
    val route: String,
    val color: androidx.compose.ui.graphics.Color,
    val score: Int
)

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String
)
