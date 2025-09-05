package com.hellogerman.app.ui.utils

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Utility class for responsive design handling
 */
object ResponsiveUtils {
    
    /**
     * Determines if the current screen is in landscape orientation
     */
    @Composable
    fun isLandscape(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    }
    
    /**
     * Determines if the current screen is a tablet (width >= 600dp)
     */
    @Composable
    fun isTablet(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp >= 600
    }
    
    /**
     * Determines if the current screen is a large tablet (width >= 840dp)
     */
    @Composable
    fun isLargeTablet(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp >= 840
    }
    
    /**
     * Gets responsive padding based on screen size
     */
    @Composable
    fun getResponsivePadding(): Dp {
        return when {
            isLargeTablet() -> 32.dp
            isTablet() -> 24.dp
            else -> 16.dp
        }
    }
    
    /**
     * Gets responsive spacing based on screen size
     */
    @Composable
    fun getResponsiveSpacing(): Dp {
        return when {
            isLargeTablet() -> 24.dp
            isTablet() -> 20.dp
            else -> 16.dp
        }
    }
    
    /**
     * Gets responsive content width for tablets
     */
    @Composable
    fun getContentMaxWidth(): Dp {
        return when {
            isLargeTablet() -> 1200.dp
            isTablet() -> 800.dp
            else -> Dp.Unspecified
        }
    }
}

/**
 * Responsive layout composable that adapts to screen size and orientation
 */
@Composable
fun ResponsiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLandscape = ResponsiveUtils.isLandscape()
        val isTablet = ResponsiveUtils.isTablet()
        val isLargeTablet = ResponsiveUtils.isLargeTablet()
        
        when {
            isLargeTablet && isLandscape -> {
                // Large tablet landscape - use side-by-side layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ResponsiveUtils.getResponsivePadding())
                ) {
                    content()
                }
            }
            isTablet && isLandscape -> {
                // Tablet landscape - use side-by-side layout with smaller padding
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ResponsiveUtils.getResponsivePadding())
                ) {
                    content()
                }
            }
            else -> {
                // Phone or portrait - use single column layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ResponsiveUtils.getResponsivePadding())
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * Responsive navigation layout that adapts bottom bar for tablets
 */
@Composable
fun ResponsiveNavigationLayout(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val isTablet = ResponsiveUtils.isTablet()
    val isLandscape = ResponsiveUtils.isLandscape()
    
    if (isTablet && isLandscape) {
        // Tablet landscape - navigation on the side
        Row(modifier = modifier.fillMaxSize()) {
            // Side navigation would go here
            content()
        }
    } else {
        // Phone or tablet portrait - bottom navigation
        androidx.compose.material3.Scaffold(
            modifier = modifier,
            bottomBar = bottomBar
        ) { innerPadding ->
            content()
        }
    }
}