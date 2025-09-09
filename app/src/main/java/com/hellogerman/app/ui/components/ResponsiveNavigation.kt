package com.hellogerman.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import com.hellogerman.app.ui.navigation.Screen
import com.hellogerman.app.ui.utils.ResponsiveUtils

/**
 * Responsive navigation that adapts to different screen sizes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsiveNavigation(
    navController: NavController,
    currentDestination: androidx.navigation.NavDestination?,
    modifier: Modifier = Modifier
) {
    val isTablet = ResponsiveUtils.isTablet()
    val isLandscape = ResponsiveUtils.isLandscape()
    
    if (isTablet && isLandscape) {
        // Tablet landscape - side navigation
        SideNavigation(
            navController = navController,
            currentDestination = currentDestination,
            modifier = modifier
        )
    } else {
        // Phone or tablet portrait - bottom navigation
        BottomNavigation(
            navController = navController,
            currentDestination = currentDestination,
            modifier = modifier
        )
    }
}

@Composable
private fun SideNavigation(
    navController: NavController,
    currentDestination: androidx.navigation.NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val navItems = getNavigationItems()
        
        navItems.forEach { item ->
            NavigationRailItem(
                icon = { 
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    ) 
                },
                label = { 
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    if (item.route == Screen.Dashboard.route) {
                        // Special handling for Home button - pop to dashboard
                        navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationRailItemDefaults.colors(
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

@Composable
private fun BottomNavigation(
    navController: NavController,
    currentDestination: androidx.navigation.NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val navItems = getNavigationItems()
        
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
                    if (item.route == Screen.Dashboard.route) {
                        // Special handling for Home button - pop to dashboard
                        navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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

private fun getNavigationItems(): List<NavigationItem> {
    return listOf(
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
        ),
        NavigationItem(
            route = Screen.Sprechen.route,
            icon = Icons.Default.Mic,
            label = "Speak"
        )
    )
}

data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)