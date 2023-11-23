package com.dicoding.dietin.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Green,
    secondary = DarkGreen,
    tertiary = HeavyRed,
    surface = LineStroke,
    background = White,
    onSecondary = SubtleGrey,
    onTertiary = DarkGrey,
    onBackground = Black,
    onSurface = Grey,
)

private val LightColorScheme = lightColorScheme(
    primary = Green,
    secondary = DarkGreen,
    tertiary = HeavyRed,
    surface = LineStroke,
    background = White,
    onSecondary = SubtleGrey,
    onTertiary = DarkGrey,
    onBackground = Black,
    onSurface = Grey,
)

@Composable
fun DietInTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}