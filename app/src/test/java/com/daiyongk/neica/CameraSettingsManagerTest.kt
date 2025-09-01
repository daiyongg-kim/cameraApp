package com.daiyongk.neica

import android.content.Context
import android.content.SharedPreferences
import androidx.camera.core.ImageCapture
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CameraSettingsManagerTest {

    private lateinit var context: Context
    private lateinit var settingsManager: CameraSettingsManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        sharedPreferences = context.getSharedPreferences("camera_settings", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.clear().commit()
        settingsManager = CameraSettingsManager(context)
    }

    @Test
    fun `test default file format is JPEG`() {
        assertEquals(FileFormat.JPEG, settingsManager.fileFormat.value)
    }

    @Test
    fun `test setting file format to RAW`() {
        settingsManager.setFileFormat(FileFormat.RAW)
        assertEquals(FileFormat.RAW, settingsManager.fileFormat.value)
        assertEquals("RAW", sharedPreferences.getString("file_format", null))
    }

    @Test
    fun `test setting file format to JPEG_RAW`() {
        settingsManager.setFileFormat(FileFormat.JPEG_RAW)
        assertEquals(FileFormat.JPEG_RAW, settingsManager.fileFormat.value)
        assertEquals("JPEG_RAW", sharedPreferences.getString("file_format", null))
    }

    @Test
    fun `test default embed location is false`() {
        assertFalse(settingsManager.embedLocation.value)
    }

    @Test
    fun `test setting embed location to true`() {
        settingsManager.setEmbedLocation(true)
        assertTrue(settingsManager.embedLocation.value)
        assertTrue(sharedPreferences.getBoolean("embed_location", false))
    }

    @Test
    fun `test default flash mode is OFF`() {
        assertEquals(FlashMode.OFF, settingsManager.flashMode.value)
    }

    @Test
    fun `test setting flash mode to ON`() {
        settingsManager.setFlashMode(FlashMode.ON)
        assertEquals(FlashMode.ON, settingsManager.flashMode.value)
        assertEquals("ON", sharedPreferences.getString("flash_mode", null))
    }

    @Test
    fun `test setting flash mode to AUTO`() {
        settingsManager.setFlashMode(FlashMode.AUTO)
        assertEquals(FlashMode.AUTO, settingsManager.flashMode.value)
        assertEquals("AUTO", sharedPreferences.getString("flash_mode", null))
    }

    @Test
    fun `test getCameraXFlashMode returns correct values`() {
        settingsManager.setFlashMode(FlashMode.OFF)
        assertEquals(ImageCapture.FLASH_MODE_OFF, settingsManager.getCameraXFlashMode())

        settingsManager.setFlashMode(FlashMode.ON)
        assertEquals(ImageCapture.FLASH_MODE_ON, settingsManager.getCameraXFlashMode())

        settingsManager.setFlashMode(FlashMode.AUTO)
        assertEquals(ImageCapture.FLASH_MODE_AUTO, settingsManager.getCameraXFlashMode())
    }

    @Test
    fun `test default timer duration is OFF`() {
        assertEquals(TimerDuration.OFF, settingsManager.timerDuration.value)
    }

    @Test
    fun `test setting timer duration to THREE`() {
        settingsManager.setTimerDuration(TimerDuration.THREE)
        assertEquals(TimerDuration.THREE, settingsManager.timerDuration.value)
        assertEquals("THREE", sharedPreferences.getString("timer_duration", null))
    }

    @Test
    fun `test setting timer duration to TEN`() {
        settingsManager.setTimerDuration(TimerDuration.TEN)
        assertEquals(TimerDuration.TEN, settingsManager.timerDuration.value)
        assertEquals("TEN", sharedPreferences.getString("timer_duration", null))
    }

    @Test
    fun `test default show grid is false`() {
        assertFalse(settingsManager.showGrid.value)
    }

    @Test
    fun `test toggling show grid`() {
        // Initial state should be false
        assertFalse(settingsManager.showGrid.value)
        
        // Toggle to true
        settingsManager.toggleGrid()
        assertTrue(settingsManager.showGrid.value)
        assertTrue(sharedPreferences.getBoolean("show_grid", false))
        
        // Toggle back to false
        settingsManager.toggleGrid()
        assertFalse(settingsManager.showGrid.value)
        assertFalse(sharedPreferences.getBoolean("show_grid", false))
    }

    @Test
    fun `test default show level is false`() {
        assertFalse(settingsManager.showLevel.value)
    }

    @Test
    fun `test toggling show level`() {
        // Initial state should be false
        assertFalse(settingsManager.showLevel.value)
        
        // Toggle to true
        settingsManager.toggleLevel()
        assertTrue(settingsManager.showLevel.value)
        assertTrue(sharedPreferences.getBoolean("show_level", false))
        
        // Toggle back to false
        settingsManager.toggleLevel()
        assertFalse(settingsManager.showLevel.value)
        assertFalse(sharedPreferences.getBoolean("show_level", false))
    }

    @Test
    fun `test default show histogram is false`() {
        assertFalse(settingsManager.showHistogram.value)
    }

    @Test
    fun `test toggling show histogram`() {
        // Initial state should be false
        assertFalse(settingsManager.showHistogram.value)
        
        // Toggle to true
        settingsManager.toggleHistogram()
        assertTrue(settingsManager.showHistogram.value)
        assertTrue(sharedPreferences.getBoolean("show_histogram", false))
        
        // Toggle back to false
        settingsManager.toggleHistogram()
        assertFalse(settingsManager.showHistogram.value)
        assertFalse(sharedPreferences.getBoolean("show_histogram", false))
    }


    @Test
    fun `test persistence across instances`() {
        // Set values in first instance
        settingsManager.setFileFormat(FileFormat.RAW)
        settingsManager.setEmbedLocation(true)
        settingsManager.setFlashMode(FlashMode.AUTO)
        settingsManager.setTimerDuration(TimerDuration.TEN)
        settingsManager.toggleGrid() // Toggle from false to true
        settingsManager.toggleLevel() // Toggle from false to true

        // Create new instance
        val newSettingsManager = CameraSettingsManager(context)

        // Verify values are persisted
        assertEquals(FileFormat.RAW, newSettingsManager.fileFormat.value)
        assertTrue(newSettingsManager.embedLocation.value)
        assertEquals(FlashMode.AUTO, newSettingsManager.flashMode.value)
        assertEquals(TimerDuration.TEN, newSettingsManager.timerDuration.value)
        assertTrue(newSettingsManager.showGrid.value)
        assertTrue(newSettingsManager.showLevel.value)
    }

    @Test
    fun `test FileFormat enum values`() {
        assertEquals(3, FileFormat.values().size)
        assertEquals("JPEG", FileFormat.JPEG.name)
        assertEquals("RAW", FileFormat.RAW.name)
        assertEquals("JPEG_RAW", FileFormat.JPEG_RAW.name)
    }

    @Test
    fun `test FlashMode enum values`() {
        assertEquals(3, FlashMode.values().size)
        assertEquals("OFF", FlashMode.OFF.name)
        assertEquals("ON", FlashMode.ON.name)
        assertEquals("AUTO", FlashMode.AUTO.name)
    }

    @Test
    fun `test TimerDuration enum values and seconds`() {
        assertEquals(3, TimerDuration.values().size)
        assertEquals(0, TimerDuration.OFF.seconds)
        assertEquals(3, TimerDuration.THREE.seconds)
        assertEquals(10, TimerDuration.TEN.seconds)
    }
}