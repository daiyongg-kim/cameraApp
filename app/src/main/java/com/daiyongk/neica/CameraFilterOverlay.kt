package com.daiyongk.neica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Camera filter overlay that provides visual preview of the selected film effect
 */
@Composable
fun CameraFilterOverlay(
    filmEffect: FilmEffect,
    strength: Float,
    modifier: Modifier = Modifier
) {
    val overlayColor = filmEffect.getPreviewOverlay(strength)
    val gradientColors = getFilterGradient(filmEffect, strength)
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = if (gradientColors != null) {
                    Brush.radialGradient(
                        colors = gradientColors,
                        radius = 1200f
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(overlayColor, Color.Transparent)
                    )
                }
            )
    )
}

/**
 * Get gradient colors for specific film effects
 */
private fun getFilterGradient(filmEffect: FilmEffect, strength: Float): List<Color>? {
    val alpha = (strength / 100f * 0.08f).coerceIn(0f, 0.12f)
    
    return when (filmEffect.shortName) {
        "VIV" -> listOf(
            Color.Cyan.copy(alpha = alpha * 0.7f),
            Color.Transparent,
            Color.Magenta.copy(alpha = alpha * 0.5f)
        )
        "CHR" -> listOf(
            Color.Gray.copy(alpha = alpha),
            Color.Transparent,
            Color.Black.copy(alpha = alpha * 0.3f)
        )
        "CLS" -> listOf(
            Color(0xFFD2691E).copy(alpha = alpha), // Sepia
            Color.Transparent,
            Color(0xFF8B4513).copy(alpha = alpha * 0.5f) // Saddle brown
        )
        "CNT" -> listOf(
            Color.Blue.copy(alpha = alpha * 0.5f),
            Color.Transparent,
            Color.Cyan.copy(alpha = alpha * 0.3f)
        )
        else -> null
    }
}