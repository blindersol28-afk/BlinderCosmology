package com.blindercosmology.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Serif = FontFamily.Serif

val BlinderType = Typography(
    displayLarge = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 36.sp, letterSpacing = 1.5.sp, color = Sepia),
    displayMedium = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 1.2.sp, color = Sepia),
    headlineLarge = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = 1.sp, color = Sepia),
    headlineMedium = TextStyle(fontFamily = Serif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 0.8.sp, color = Bone),
    titleLarge = TextStyle(fontFamily = Serif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, letterSpacing = 0.6.sp, color = Bone),
    titleMedium = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Bone),
    bodyLarge = TextStyle(fontFamily = Serif, fontSize = 16.sp, lineHeight = 24.sp, color = Bone),
    bodyMedium = TextStyle(fontFamily = Serif, fontSize = 14.sp, lineHeight = 20.sp, color = Bone),
    labelLarge = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp, color = Sepia),
)
