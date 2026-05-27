package com.blindercosmology.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = Sepia,
    onPrimary = InkBlack,
    secondary = Brass,
    onSecondary = InkBlack,
    tertiary = Blood,
    onTertiary = Bone,
    background = InkBlack,
    onBackground = Bone,
    surface = Smoke,
    onSurface = Bone,
    surfaceVariant = SmokeLight,
    onSurfaceVariant = Bone,
    outline = Brass,
)

@Composable
fun BlinderTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = BlinderType,
        content = content,
    )
}
