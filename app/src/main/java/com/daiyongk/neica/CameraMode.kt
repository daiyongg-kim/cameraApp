package com.daiyongk.neica

import androidx.compose.ui.graphics.Color

sealed class CameraMode(
    val displayName: String,
    val shortName: String,
    val description: String
) {
    object Photo : CameraMode("PHOTO", "P", "Standard photo capture mode")
    object Aperture : CameraMode("APERTURE", "A", "Aperture priority mode for depth control")
}

data class CameraSetting(
    val name: String,
    val iconName: String,
    val isEnabled: Boolean = true,
    val hasProBadge: Boolean = false
)

object CameraSettings {
    val FORMAT = CameraSetting("Format", "format", true)
    val EMBED_LOCATION = CameraSetting("Embed Location", "location", true)
    val FLASH = CameraSetting("Flash", "flash", true)
    val TIMER = CameraSetting("Timer", "timer", true)
    val GRID = CameraSetting("Grid", "grid", true)
    val LEVEL = CameraSetting("Level", "level", true)
    val FOCUS_PEAKING = CameraSetting("Focus Peaking", "focus", true, true)
    val HISTOGRAM = CameraSetting("Histogram", "histogram", true, true)
    
    fun getAllSettings() = listOf(
        FORMAT, EMBED_LOCATION, FLASH, TIMER,
        GRID, LEVEL, FOCUS_PEAKING, HISTOGRAM
    )
}