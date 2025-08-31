package com.hellogerman.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.viewmodel.SettingsViewModel
import com.hellogerman.app.data.repository.OfflineCacheManager
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.remember
import com.hellogerman.app.data.DatabaseInitializer
import androidx.compose.ui.platform.LocalContext
import com.hellogerman.app.data.repository.HelloGermanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val textSize by settingsViewModel.textSize.collectAsState()
    val dailyGoal by settingsViewModel.dailyGoal.collectAsState()
    val showEnglishExplanations by settingsViewModel.showEnglishExplanations.collectAsState()

    // Cache management
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var cacheStats by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    // Cache manager instance
    val cacheManager = OfflineCacheManager(navController.context)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Appearance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            item {
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Dark Mode",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dark Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Use dark theme",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { settingsViewModel.setDarkMode(it) }
                        )
                    }
                }
            }

            item {
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "English Explanations",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "English Explanations",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Show English translations and explanations",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = showEnglishExplanations,
                            onCheckedChange = { settingsViewModel.setEnglishExplanations(it) }
                        )
                    }
                }
            }

            // Cache Management Section
            item {
                Text(
                    text = "Storage & Cache",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Cache Statistics",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Display cache stats
                        if (cacheStats.isNotEmpty()) {
                            Text(
                                text = "Compressed Files: ${cacheStats["compressed_files"] ?: 0}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Cache Files: ${cacheStats["cache_files"] ?: 0}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Total Size: ${cacheStats["total_size_kb"] ?: 0} KB",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        cacheStats = cacheManager.getCacheStats()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Refresh Stats", fontSize = 14.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val deletedCount = cacheManager.cleanupOldCache()
                                        snackbarHostState.showSnackbar("Cleaned up $deletedCount old cache files")
                                        cacheStats = cacheManager.getCacheStats()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Clean Old", fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    val success = cacheManager.clearAllCache()
                                    val message = if (success) "Cache cleared successfully" else "Failed to clear cache"
                                    snackbarHostState.showSnackbar(message)
                                    cacheStats = cacheManager.getCacheStats()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reload Lessons", fontSize = 14.sp)
                        }

                        // Force reload lessons button
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    DatabaseInitializer.forceReloadLessons(navController.context)
                                    snackbarHostState.showSnackbar("Lessons reloaded successfully")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Force Reload Lessons", fontSize = 14.sp)
                        }

                        // Debug button to check lesson counts from database
                        OutlinedButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val repository = HelloGermanRepository(navController.context)
                                    val allLessons = repository.getAllLessons()
                                    val a1Count = allLessons.filter { it.level == "A1" }.size
                                    val a2Count = allLessons.filter { it.level == "A2" }.size
                                    val b1Count = allLessons.filter { it.level == "B1" }.size
                                    val lesenA2Count = allLessons.filter { it.level == "A2" && it.skill == "lesen" }.size
                                    val hoerenA2Count = allLessons.filter { it.level == "A2" && it.skill == "hoeren" }.size
                                    val schreibenA2Count = allLessons.filter { it.level == "A2" && it.skill == "schreiben" }.size
                                    val sprechenA2Count = allLessons.filter { it.level == "A2" && it.skill == "sprechen" }.size
                                    val lesenB1Count = allLessons.filter { it.level == "B1" && it.skill == "lesen" }.size
                                    val hoerenB1Count = allLessons.filter { it.level == "B1" && it.skill == "hoeren" }.size
                                    val schreibenB1Count = allLessons.filter { it.level == "B1" && it.skill == "schreiben" }.size
                                    val sprechenB1Count = allLessons.filter { it.level == "B1" && it.skill == "sprechen" }.size

                                    withContext(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar(
                                            "DB: Total: ${allLessons.size}, A1: $a1Count, A2: $a2Count, B1: $b1Count | " +
                                            "A2(L:$lesenA2Count H:$hoerenA2Count S:$schreibenA2Count Sp:$sprechenA2Count) | " +
                                            "B1(L:$lesenB1Count H:$hoerenB1Count S:$schreibenB1Count Sp:$sprechenB1Count)"
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Check DB Lesson Counts", fontSize = 14.sp)
                        }

                        // Debug button to check generated lesson counts (not from DB)
                        OutlinedButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val debugInfo = com.hellogerman.app.data.LessonContentGenerator.debugCountLessons()
                                    android.util.Log.d("LessonDebug", debugInfo)

                                    withContext(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar("Lesson debug info logged to console")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Debug All Lesson Counts", fontSize = 14.sp)
                        }

                        // Debug button to check B1 lesson distribution
                        OutlinedButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val b1DebugInfo = com.hellogerman.app.data.LessonContentGenerator.debugB1LessonDistribution()
                                    android.util.Log.d("B1LessonDebug", b1DebugInfo)

                                    withContext(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar("B1 lesson distribution logged to console")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Check Lesson Counts", fontSize = 14.sp)
                        }

                        // Quick count button
                        OutlinedButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val (b1Count, skillDistribution, totalCount) = com.hellogerman.app.data.LessonContentGenerator.countB1Lessons()

                                    withContext(Dispatchers.Main) {
                                        snackbarHostState.showSnackbar(
                                            "B1: ${b1Count} lessons | L:${skillDistribution["lesen"] ?: 0} H:${skillDistribution["hoeren"] ?: 0} S:${skillDistribution["schreiben"] ?: 0} Sp:${skillDistribution["sprechen"] ?: 0} | Total: $totalCount"
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Quick B1 Count", fontSize = 14.sp)
                        }

                        // Clear all cache button
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val success = cacheManager.clearAllCache()
                                    val message = if (success) "Cache cleared successfully" else "Failed to clear cache"
                                    snackbarHostState.showSnackbar(message)
                                    cacheStats = cacheManager.getCacheStats()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Clear All Cache", fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Refreshing lessons... This may take a moment.")
                                    DatabaseInitializer.forceReloadLessons(context)
                                    snackbarHostState.showSnackbar("Lessons refreshed! Restart the app to see changes.")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Refresh All Lessons", fontSize = 14.sp)
                        }
                    }
                }
            }

            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Text Size",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Text Size",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Slider(
                            value = textSize,
                            onValueChange = { settingsViewModel.setTextSize(it) },
                            valueRange = 0.8f..1.4f,
                            steps = 5
                        )
                        Text(
                            text = "Size: ${(textSize * 100).toInt()}%",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            

            
            item {
                Text(
                    text = "Learning Goals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Daily Goal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3, 5, 10).forEach { goal ->
                                FilterChip(
                                    onClick = { settingsViewModel.setDailyGoal(goal) },
                                    label = { Text("$goal lessons") },
                                    selected = dailyGoal == goal
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "About",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Version",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Hello German",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Version 1.0.0",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Privacy",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Privacy Policy",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
fun SettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}
