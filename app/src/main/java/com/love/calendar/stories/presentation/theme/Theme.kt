// app/src/main/java/com/love/calendar/stories/presentation/theme/Theme.kt
package com.love.calendar.stories.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF58a6ff),
    secondary = Color(0xFFff6b9d),
    tertiary = Color(0xFFf6d365),
    background = Color(0xFF0a0c10),
    surface = Color(0xFF1c2128),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun LoveCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}