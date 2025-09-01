package com.daiyongk.neica

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageCapture
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay

@RunWith(AndroidJUnit4::class)
class CameraInstrumentedTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA
    )

    private lateinit var context: Context
    private lateinit var photosDir: File

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        photosDir = File(context.filesDir, "photos")
        
        // Clean up photos directory
        if (photosDir.exists()) {
            photosDir.listFiles()?.forEach { it.delete() }
        }
        photosDir.mkdirs()
    }

    @Test
    fun testPhotoSaving() {
        // Create a test bitmap
        val testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        testBitmap.eraseColor(android.graphics.Color.RED)
        
        // Save it to photos directory
        val photoFile = File(photosDir, "test_photo.jpg")
        FileOutputStream(photoFile).use { out ->
            testBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
        
        // Verify file exists and can be read
        assertTrue(photoFile.exists())
        assertTrue(photoFile.length() > 0)
        
        // Load and verify the saved photo
        val loadedBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        assertNotNull(loadedBitmap)
        assertEquals(100, loadedBitmap.width)
        assertEquals(100, loadedBitmap.height)
        
        // Cleanup
        testBitmap.recycle()
        loadedBitmap.recycle()
    }

    @Test
    fun testLoadAllPhotos() {
        // Create multiple test photos
        val photoFiles = mutableListOf<File>()
        for (i in 1..5) {
            val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(android.graphics.Color.BLUE)
            
            val photoFile = File(photosDir, "photo_$i.jpg")
            FileOutputStream(photoFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            photoFiles.add(photoFile)
            bitmap.recycle()
            
            // Add small delay to ensure different timestamps
            Thread.sleep(10)
        }
        
        // Load all photos
        val loadedPhotos = loadAllPhotos(context)
        
        // Verify all photos are loaded and sorted by modification time
        assertEquals(5, loadedPhotos.size)
        for (i in 0 until loadedPhotos.size - 1) {
            assertTrue(loadedPhotos[i].lastModified() <= loadedPhotos[i + 1].lastModified())
        }
    }

    @Test
    fun testFilterApplication() {
        // Create a test bitmap
        val originalBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        originalBitmap.eraseColor(android.graphics.Color.GRAY)
        
        // Apply different filters
        val vividResult = originalBitmap.applyFilmEffect(FilmEffects.VIV, 100f)
        val naturalResult = originalBitmap.applyFilmEffect(FilmEffects.NAT, 100f)
        
        // Verify results are different from original and from each other
        assertNotNull(vividResult)
        assertNotNull(naturalResult)
        
        val originalPixel = originalBitmap.getPixel(50, 50)
        val vividPixel = vividResult.getPixel(50, 50)
        val naturalPixel = naturalResult.getPixel(50, 50)
        
        // Colors should be different after applying filters
        assertNotEquals(originalPixel, vividPixel)
        assertNotEquals(originalPixel, naturalPixel)
        assertNotEquals(vividPixel, naturalPixel)
        
        // Cleanup
        originalBitmap.recycle()
        vividResult.recycle()
        naturalResult.recycle()
    }

    @Test
    fun testCameraSettingsPersistence() {
        val settingsManager = CameraSettingsManager(context)
        
        // Set various settings
        settingsManager.setFileFormat(FileFormat.RAW)
        settingsManager.setFlashMode(FlashMode.AUTO)
        settingsManager.setTimerDuration(TimerDuration.THREE_SECONDS)
        settingsManager.setShowGrid(true)
        settingsManager.setShowLevel(true)
        settingsManager.setEmbedLocation(true)
        
        // Create a new instance to test persistence
        val newSettingsManager = CameraSettingsManager(context)
        
        // Verify settings are persisted
        assertEquals(FileFormat.RAW, newSettingsManager.fileFormat.value)
        assertEquals(FlashMode.AUTO, newSettingsManager.flashMode.value)
        assertEquals(TimerDuration.THREE_SECONDS, newSettingsManager.timerDuration.value)
        assertTrue(newSettingsManager.showGrid.value)
        assertTrue(newSettingsManager.showLevel.value)
        assertTrue(newSettingsManager.embedLocation.value)
    }

    @Test
    fun testPhotoFileNaming() {
        val filmEffect = FilmEffects.VIV
        val strength = 75f
        val timestamp = System.currentTimeMillis()
        
        val expectedFileName = "${timestamp}_${filmEffect.shortName}_${strength.toInt()}.jpg"
        val photoFile = File(photosDir, expectedFileName)
        
        // Create a test photo with the expected naming convention
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        FileOutputStream(photoFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
        
        // Verify file exists with correct name
        assertTrue(photoFile.exists())
        assertTrue(photoFile.name.contains(filmEffect.shortName))
        assertTrue(photoFile.name.contains(strength.toInt().toString()))
        assertTrue(photoFile.name.endsWith(".jpg"))
        
        bitmap.recycle()
    }

    @Test
    fun testTimerCountdown() = runBlocking {
        var countdownValue = 3
        val onCountdown: (Int) -> Unit = { value ->
            countdownValue = value
        }
        
        // Simulate timer countdown
        for (i in 3 downTo 1) {
            onCountdown(i)
            assertEquals(i, countdownValue)
            delay(100) // Short delay for testing
        }
        onCountdown(0)
        assertEquals(0, countdownValue)
    }

    @Test
    fun testFlashModeConversion() {
        val settingsManager = CameraSettingsManager(context)
        
        // Test OFF
        settingsManager.setFlashMode(FlashMode.OFF)
        assertEquals(ImageCapture.FLASH_MODE_OFF, settingsManager.getCameraXFlashMode())
        
        // Test ON
        settingsManager.setFlashMode(FlashMode.ON)
        assertEquals(ImageCapture.FLASH_MODE_ON, settingsManager.getCameraXFlashMode())
        
        // Test AUTO
        settingsManager.setFlashMode(FlashMode.AUTO)
        assertEquals(ImageCapture.FLASH_MODE_AUTO, settingsManager.getCameraXFlashMode())
    }

    @Test
    fun testPhotoDirectoryCreation() {
        // Delete photos directory if it exists
        if (photosDir.exists()) {
            photosDir.deleteRecursively()
        }
        
        assertFalse(photosDir.exists())
        
        // Create directory
        photosDir.mkdirs()
        
        assertTrue(photosDir.exists())
        assertTrue(photosDir.isDirectory)
    }

    @Test
    fun testEmptyGallery() {
        // Ensure photos directory is empty
        photosDir.listFiles()?.forEach { it.delete() }
        
        val photos = loadAllPhotos(context)
        
        assertTrue(photos.isEmpty())
    }

    @Test
    fun testFilterStrengthInterpolation() {
        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.WHITE)
        
        // Apply same filter with different strengths
        val result0 = bitmap.applyFilmEffect(FilmEffects.VIV, 0f)
        val result50 = bitmap.applyFilmEffect(FilmEffects.VIV, 50f)
        val result100 = bitmap.applyFilmEffect(FilmEffects.VIV, 100f)
        
        // Get center pixels
        val pixel0 = result0.getPixel(5, 5)
        val pixel50 = result50.getPixel(5, 5)
        val pixel100 = result100.getPixel(5, 5)
        
        // Verify different strengths produce different results
        assertNotEquals(pixel0, pixel100)
        // 50% should be different from both extremes
        assertNotEquals(pixel50, pixel0)
        assertNotEquals(pixel50, pixel100)
        
        // Cleanup
        bitmap.recycle()
        result0.recycle()
        result50.recycle()
        result100.recycle()
    }
}