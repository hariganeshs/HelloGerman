package com.hellogerman.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.hellogerman.app.ui.utils.ResponsiveUtils

/**
 * Responsive theme that adapts to different screen sizes
 */
@Composable
fun ResponsiveTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val isTablet = ResponsiveUtils.isTablet()
    val isLargeTablet = ResponsiveUtils.isLargeTablet()
    
    val shapes = when {
        isLargeTablet -> Shapes(
            small = RoundedCornerShape(20.dp),
            medium = RoundedCornerShape(24.dp),
            large = RoundedCornerShape(28.dp)
        )
        isTablet -> Shapes(
            small = RoundedCornerShape(16.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(24.dp)
        )
        else -> Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(20.dp)
        )
    }
    
    val typography = when {
        isLargeTablet -> Typography(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            displayMedium = MaterialTheme.typography.displayMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            displaySmall = MaterialTheme.typography.displaySmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified)
        )
        isTablet -> Typography(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            displayMedium = MaterialTheme.typography.displayMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            displaySmall = MaterialTheme.typography.displaySmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified)
        )
        else -> MaterialTheme.typography
    }
    
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}