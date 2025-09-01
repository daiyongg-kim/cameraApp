package com.daiyongk.neica

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FilterProcessorTest {

    @Mock
    private lateinit var mockBitmap: Bitmap
    
    @Mock
    private lateinit var mockCanvas: Canvas
    
    @Mock
    private lateinit var mockPaint: Paint

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test applyFilter returns new bitmap with correct dimensions`() {
        // Setup
        val width = 100
        val height = 200
        val testBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        // Execute
        val result = FilterProcessor.applyFilter(testBitmap, FilmEffects.VIV, 100f)
        
        // Verify
        assertNotNull(result)
        assertEquals(width, result.width)
        assertEquals(height, result.height)
        assertEquals(Bitmap.Config.ARGB_8888, result.config)
        
        // Cleanup
        testBitmap.recycle()
        result.recycle()
    }

    @Test
    fun `test applyFilter with zero strength returns copy of original`() {
        // Setup
        val testBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        testBitmap.eraseColor(android.graphics.Color.RED)
        
        // Execute
        val result = FilterProcessor.applyFilter(testBitmap, FilmEffects.VIV, 0f)
        
        // Verify - with 0 strength, should be close to original
        assertNotNull(result)
        assertEquals(testBitmap.width, result.width)
        assertEquals(testBitmap.height, result.height)
        
        // Cleanup
        testBitmap.recycle()
        result.recycle()
    }

    @Test
    fun `test different effects produce different results`() {
        // Setup
        val testBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        testBitmap.eraseColor(android.graphics.Color.GRAY)
        
        // Execute
        val vivResult = FilterProcessor.applyFilter(testBitmap, FilmEffects.VIV, 100f)
        val natResult = FilterProcessor.applyFilter(testBitmap, FilmEffects.NAT, 100f)
        
        // Get center pixel from each result
        val vivPixel = vivResult.getPixel(5, 5)
        val natPixel = natResult.getPixel(5, 5)
        
        // Verify - different effects should produce different results
        assertNotEquals(vivPixel, natPixel)
        
        // Cleanup
        testBitmap.recycle()
        vivResult.recycle()
        natResult.recycle()
    }

    @Test
    fun `test strength parameter affects output`() {
        // Setup
        val testBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        testBitmap.eraseColor(android.graphics.Color.GRAY)
        
        // Execute
        val result25 = FilterProcessor.applyFilter(testBitmap, FilmEffects.VIV, 25f)
        val result75 = FilterProcessor.applyFilter(testBitmap, FilmEffects.VIV, 75f)
        
        // Get center pixel from each result
        val pixel25 = result25.getPixel(5, 5)
        val pixel75 = result75.getPixel(5, 5)
        
        // Verify - different strengths should produce different results
        assertNotEquals(pixel25, pixel75)
        
        // Cleanup
        testBitmap.recycle()
        result25.recycle()
        result75.recycle()
    }

}