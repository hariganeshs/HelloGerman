package com.hellogerman.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnSecondary,
    onTertiary = DarkOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Ocean Theme Color Schemes
private val OceanLightColorScheme = lightColorScheme(
    primary = OceanPrimary,
    secondary = OceanSecondary,
    tertiary = OceanTertiary,
    background = OceanBackground,
    surface = OceanSurface,
    onPrimary = OceanOnPrimary,
    onSecondary = OceanOnSecondary,
    onTertiary = OceanOnTertiary,
    onBackground = OceanOnBackground,
    onSurface = OceanOnSurface,
)

private val OceanDarkColorScheme = darkColorScheme(
    primary = OceanPrimary,
    secondary = OceanSecondary,
    tertiary = OceanTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = OceanOnPrimary,
    onSecondary = OceanOnSecondary,
    onTertiary = OceanOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Forest Theme Color Schemes
private val ForestLightColorScheme = lightColorScheme(
    primary = ForestPrimary,
    secondary = ForestSecondary,
    tertiary = ForestTertiary,
    background = ForestBackground,
    surface = ForestSurface,
    onPrimary = ForestOnPrimary,
    onSecondary = ForestOnSecondary,
    onTertiary = ForestOnTertiary,
    onBackground = ForestOnBackground,
    onSurface = ForestOnSurface,
)

private val ForestDarkColorScheme = darkColorScheme(
    primary = ForestPrimary,
    secondary = ForestSecondary,
    tertiary = ForestTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = ForestOnPrimary,
    onSecondary = ForestOnSecondary,
    onTertiary = ForestOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Sunset Theme Color Schemes
private val SunsetLightColorScheme = lightColorScheme(
    primary = SunsetPrimary,
    secondary = SunsetSecondary,
    tertiary = SunsetTertiary,
    background = SunsetBackground,
    surface = SunsetSurface,
    onPrimary = SunsetOnPrimary,
    onSecondary = SunsetOnSecondary,
    onTertiary = SunsetOnTertiary,
    onBackground = SunsetOnBackground,
    onSurface = SunsetOnSurface,
)

private val SunsetDarkColorScheme = darkColorScheme(
    primary = SunsetPrimary,
    secondary = SunsetSecondary,
    tertiary = SunsetTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = SunsetOnPrimary,
    onSecondary = SunsetOnSecondary,
    onTertiary = SunsetOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Mountain Theme Color Schemes
private val MountainLightColorScheme = lightColorScheme(
    primary = MountainPrimary,
    secondary = MountainSecondary,
    tertiary = MountainTertiary,
    background = MountainBackground,
    surface = MountainSurface,
    onPrimary = MountainOnPrimary,
    onSecondary = MountainOnSecondary,
    onTertiary = MountainOnTertiary,
    onBackground = MountainOnBackground,
    onSurface = MountainOnSurface,
)

private val MountainDarkColorScheme = darkColorScheme(
    primary = MountainPrimary,
    secondary = MountainSecondary,
    tertiary = MountainTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = MountainOnPrimary,
    onSecondary = MountainOnSecondary,
    onTertiary = MountainOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Desert Theme Color Schemes
private val DesertLightColorScheme = lightColorScheme(
    primary = DesertPrimary,
    secondary = DesertSecondary,
    tertiary = DesertTertiary,
    background = DesertBackground,
    surface = DesertSurface,
    onPrimary = DesertOnPrimary,
    onSecondary = DesertOnSecondary,
    onTertiary = DesertOnTertiary,
    onBackground = DesertOnBackground,
    onSurface = DesertOnSurface,
)

private val DesertDarkColorScheme = darkColorScheme(
    primary = DesertPrimary,
    secondary = DesertSecondary,
    tertiary = DesertTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DesertOnPrimary,
    onSecondary = DesertOnSecondary,
    onTertiary = DesertOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Space Theme (primarily dark theme)
private val SpaceColorScheme = darkColorScheme(
    primary = SpacePrimary,
    secondary = SpaceSecondary,
    tertiary = SpaceTertiary,
    background = SpaceBackground,
    surface = SpaceSurface,
    onPrimary = SpaceOnPrimary,
    onSecondary = SpaceOnSecondary,
    onTertiary = SpaceOnTertiary,
    onBackground = SpaceOnBackground,
    onSurface = SpaceOnSurface,
)

// Retro Theme Color Schemes
private val RetroLightColorScheme = lightColorScheme(
    primary = RetroPrimary,
    secondary = RetroSecondary,
    tertiary = RetroTertiary,
    background = RetroBackground,
    surface = RetroSurface,
    onPrimary = RetroOnPrimary,
    onSecondary = RetroOnSecondary,
    onTertiary = RetroOnTertiary,
    onBackground = RetroOnBackground,
    onSurface = RetroOnSurface,
)

private val RetroDarkColorScheme = darkColorScheme(
    primary = RetroPrimary,
    secondary = RetroSecondary,
    tertiary = RetroTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = RetroOnPrimary,
    onSecondary = RetroOnSecondary,
    onTertiary = RetroOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Minimalist Theme Color Schemes
private val MinimalistLightColorScheme = lightColorScheme(
    primary = MinimalistPrimary,
    secondary = MinimalistSecondary,
    tertiary = MinimalistTertiary,
    background = MinimalistBackground,
    surface = MinimalistSurface,
    onPrimary = MinimalistOnPrimary,
    onSecondary = MinimalistOnSecondary,
    onTertiary = MinimalistOnTertiary,
    onBackground = MinimalistOnBackground,
    onSurface = MinimalistOnSurface,
)

private val MinimalistDarkColorScheme = darkColorScheme(
    primary = MinimalistPrimary,
    secondary = MinimalistSecondary,
    tertiary = MinimalistTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = MinimalistOnPrimary,
    onSecondary = MinimalistOnSecondary,
    onTertiary = MinimalistOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Autumn Theme Color Schemes
private val AutumnLightColorScheme = lightColorScheme(
    primary = AutumnPrimary,
    secondary = AutumnSecondary,
    tertiary = AutumnTertiary,
    background = AutumnBackground,
    surface = AutumnSurface,
    onPrimary = AutumnOnPrimary,
    onSecondary = AutumnOnSecondary,
    onTertiary = AutumnOnTertiary,
    onBackground = AutumnOnBackground,
    onSurface = AutumnOnSurface,
)

private val AutumnDarkColorScheme = darkColorScheme(
    primary = AutumnPrimary,
    secondary = AutumnSecondary,
    tertiary = AutumnTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = AutumnOnPrimary,
    onSecondary = AutumnOnSecondary,
    onTertiary = AutumnOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

// Winter Theme Color Schemes
private val WinterLightColorScheme = lightColorScheme(
    primary = WinterPrimary,
    secondary = WinterSecondary,
    tertiary = WinterTertiary,
    background = WinterBackground,
    surface = WinterSurface,
    onPrimary = WinterOnPrimary,
    onSecondary = WinterOnSecondary,
    onTertiary = WinterOnTertiary,
    onBackground = WinterOnBackground,
    onSurface = WinterOnSurface,
)

private val WinterDarkColorScheme = darkColorScheme(
    primary = WinterPrimary,
    secondary = WinterSecondary,
    tertiary = WinterTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = WinterOnPrimary,
    onSecondary = WinterOnSecondary,
    onTertiary = WinterOnTertiary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnSecondary,
    onTertiary = LightOnTertiary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
)

/**
 * Get the appropriate color scheme based on theme name and dark mode
 */
fun getThemeColorScheme(theme: String, isDark: Boolean): ColorScheme {
    return when (theme) {
        "ocean" -> if (isDark) OceanDarkColorScheme else OceanLightColorScheme
        "forest" -> if (isDark) ForestDarkColorScheme else ForestLightColorScheme
        "sunset" -> if (isDark) SunsetDarkColorScheme else SunsetLightColorScheme
        "mountain" -> if (isDark) MountainDarkColorScheme else MountainLightColorScheme
        "desert" -> if (isDark) DesertDarkColorScheme else DesertLightColorScheme
        "space" -> SpaceColorScheme // Space theme is primarily dark
        "retro" -> if (isDark) RetroDarkColorScheme else RetroLightColorScheme
        "minimalist" -> if (isDark) MinimalistDarkColorScheme else MinimalistLightColorScheme
        "autumn" -> if (isDark) AutumnDarkColorScheme else AutumnLightColorScheme
        "winter" -> if (isDark) WinterDarkColorScheme else WinterLightColorScheme
        else -> if (isDark) DarkColorScheme else LightColorScheme // Default theme
    }
}

@Composable
fun HelloGermanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: String = "default",
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && theme == "default" -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        theme != "default" -> getThemeColorScheme(theme, darkTheme)

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use WindowCompat for status bar handling instead of deprecated statusBarColor
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}