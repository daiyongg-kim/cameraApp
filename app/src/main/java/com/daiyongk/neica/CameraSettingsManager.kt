package com.daiyongk.neica

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.camera.core.ImageCapture

/**
 * Manages camera settings state and persistence
 */
class CameraSettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("camera_settings", Context.MODE_PRIVATE)
    
    // File Format Settings
    private val _fileFormat = mutableStateOf(loadFileFormat())
    val fileFormat: State<FileFormat> = _fileFormat
    
    // Location Settings
    private val _embedLocation = mutableStateOf(loadEmbedLocation())
    val embedLocation: State<Boolean> = _embedLocation
    
    // Flash Settings
    private val _flashMode = mutableStateOf(loadFlashMode())
    val flashMode: State<FlashMode> = _flashMode
    
    // Timer Settings
    private val _timerDuration = mutableStateOf(loadTimerDuration())
    val timerDuration: State<TimerDuration> = _timerDuration
    
    // Grid Settings
    private val _showGrid = mutableStateOf(loadShowGrid())
    val showGrid: State<Boolean> = _showGrid
    
    // Level Settings
    private val _showLevel = mutableStateOf(loadShowLevel())
    val showLevel: State<Boolean> = _showLevel
    
    // Pro Features
    private val _focusPeaking = mutableStateOf(loadFocusPeaking())
    val focusPeaking: State<Boolean> = _focusPeaking
    
    private val _showHistogram = mutableStateOf(loadShowHistogram())
    val showHistogram: State<Boolean> = _showHistogram
    
    // File Format Functions
    fun setFileFormat(format: FileFormat) {
        _fileFormat.value = format
        prefs.edit().putString("file_format", format.name).apply()
    }
    
    private fun loadFileFormat(): FileFormat {
        val formatName = prefs.getString("file_format", FileFormat.JPEG.name) ?: FileFormat.JPEG.name
        return FileFormat.valueOf(formatName)
    }
    
    // Location Functions
    fun setEmbedLocation(enabled: Boolean) {
        _embedLocation.value = enabled
        prefs.edit().putBoolean("embed_location", enabled).apply()
    }
    
    private fun loadEmbedLocation(): Boolean {
        return prefs.getBoolean("embed_location", false)
    }
    
    // Flash Functions
    fun setFlashMode(mode: FlashMode) {
        _flashMode.value = mode
        prefs.edit().putString("flash_mode", mode.name).apply()
    }
    
    private fun loadFlashMode(): FlashMode {
        val modeName = prefs.getString("flash_mode", FlashMode.OFF.name) ?: FlashMode.OFF.name
        return FlashMode.valueOf(modeName)
    }
    
    fun getCameraXFlashMode(): Int {
        return when (_flashMode.value) {
            FlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
            FlashMode.ON -> ImageCapture.FLASH_MODE_ON
            FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
        }
    }
    
    // Timer Functions
    fun setTimerDuration(duration: TimerDuration) {
        _timerDuration.value = duration
        prefs.edit().putString("timer_duration", duration.name).apply()
    }
    
    private fun loadTimerDuration(): TimerDuration {
        val durationName = prefs.getString("timer_duration", TimerDuration.OFF.name) ?: TimerDuration.OFF.name
        return TimerDuration.valueOf(durationName)
    }
    
    // Grid Functions
    fun toggleGrid() {
        val newValue = !_showGrid.value
        _showGrid.value = newValue
        prefs.edit().putBoolean("show_grid", newValue).apply()
    }
    
    private fun loadShowGrid(): Boolean {
        return prefs.getBoolean("show_grid", false)
    }
    
    // Level Functions
    fun toggleLevel() {
        val newValue = !_showLevel.value
        _showLevel.value = newValue
        prefs.edit().putBoolean("show_level", newValue).apply()
    }
    
    private fun loadShowLevel(): Boolean {
        return prefs.getBoolean("show_level", false)
    }
    
    // Focus Peaking Functions (Pro)
    fun toggleFocusPeaking() {
        val newValue = !_focusPeaking.value
        _focusPeaking.value = newValue
        prefs.edit().putBoolean("focus_peaking", newValue).apply()
    }
    
    private fun loadFocusPeaking(): Boolean {
        return prefs.getBoolean("focus_peaking", false)
    }
    
    // Histogram Functions (Pro)
    fun toggleHistogram() {
        val newValue = !_showHistogram.value
        _showHistogram.value = newValue
        prefs.edit().putBoolean("show_histogram", newValue).apply()
    }
    
    private fun loadShowHistogram(): Boolean {
        return prefs.getBoolean("show_histogram", false)
    }
}

// Enums for Settings
enum class FileFormat(val extension: String, val displayName: String) {
    JPEG(".jpg", "JPEG"),
    RAW(".dng", "RAW (DNG)"),
    JPEG_RAW(".jpg+.dng", "JPEG + RAW")
}

enum class FlashMode(val displayName: String) {
    OFF("Off"),
    ON("On"),
    AUTO("Auto")
}

enum class TimerDuration(val seconds: Int, val displayName: String) {
    OFF(0, "Off"),
    THREE(3, "3s"),
    TEN(10, "10s")
}