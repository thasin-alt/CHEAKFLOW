package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyanGlow,
    onPrimary = Color(0xFF041E26),
    primaryContainer = Color(0xFF0E3D4D),
    onPrimaryContainer = Color(0xFFC7F8FF),
    secondary = VioletGlow,
    onSecondary = Color(0xFF1E0A40),
    secondaryContainer = Color(0xFF3B1E6D),
    onSecondaryContainer = Color(0xFFEDDCFF),
    tertiary = EmeraldSuccess,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceGlass,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceGlassHigh,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderGlass,
    outlineVariant = Color(0x1FFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = CyanAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFFAFE),
    onPrimaryContainer = Color(0xFF083344),
    secondary = VioletGlow,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF2E1065),
    tertiary = EmeraldSuccess,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurfaceGlass,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceGlassHigh,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorderGlass,
    outlineVariant = Color(0x14000000)
)

enum class ThemeMode {
    SYSTEM, DARK, LIGHT
}

@Composable
fun CheckFlowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
