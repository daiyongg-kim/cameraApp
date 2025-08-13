package com.daiyongk.neica

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Processes images with custom filters based on FilmEffect settings
 */
object FilterProcessor {
    
    /**
     * Apply film effect filter to a bitmap
     */
    fun applyFilter(
        bitmap: Bitmap,
        filmEffect: FilmEffect,
        strength: Float // 0-100
    ): Bitmap {
        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)
        val paint = Paint()
        
        // Create color matrix based on film effect
        val colorMatrix = createColorMatrix(filmEffect, strength)
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return resultBitmap
    }
    
    /**
     * Create color matrix for different film effects
     */
    private fun createColorMatrix(filmEffect: FilmEffect, strength: Float): ColorMatrix {
        val normalizedStrength = strength / 100f
        
        return when (filmEffect.shortName) {
            "VIV" -> createVividMatrix(normalizedStrength)
            "NAT" -> createNaturalMatrix(normalizedStrength)
            "CHR" -> createChromeMatrix(normalizedStrength)
            "CLS" -> createClassicMatrix(normalizedStrength)
            "CNT" -> createContemporaryMatrix(normalizedStrength)
            else -> ColorMatrix() // Identity matrix
        }
    }
    
    /**
     * VIVID: Enhanced saturation and contrast
     */
    private fun createVividMatrix(strength: Float): ColorMatrix {
        val colorMatrix = ColorMatrix()
        
        // Increase saturation
        val saturation = 1f + (0.5f * strength)
        colorMatrix.setSaturation(saturation)
        
        // Increase contrast
        val contrast = 1f + (0.3f * strength)
        val translate = (1f - contrast) / 2f * 255f
        
        val contrastMatrix = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, translate,
            0f, contrast, 0f, 0f, translate,
            0f, 0f, contrast, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        ))
        
        colorMatrix.postConcat(contrastMatrix)
        return colorMatrix
    }
    
    /**
     * NATURAL: Balanced, true-to-life colors
     */
    private fun createNaturalMatrix(strength: Float): ColorMatrix {
        val colorMatrix = ColorMatrix()
        
        // Slightly reduce saturation for natural look
        val saturation = 1f - (0.1f * strength)
        colorMatrix.setSaturation(saturation)
        
        // Subtle warm tone
        val warmMatrix = ColorMatrix(floatArrayOf(
            1f + (0.1f * strength), 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f - (0.1f * strength), 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        
        colorMatrix.postConcat(warmMatrix)
        return colorMatrix
    }
    
    /**
     * CHROME: High contrast with metallic tones
     */
    private fun createChromeMatrix(strength: Float): ColorMatrix {
        val colorMatrix = ColorMatrix()
        
        // High contrast and desaturation for metallic look
        val contrast = 1f + (0.4f * strength)
        val saturation = 1f - (0.2f * strength)
        
        colorMatrix.setSaturation(saturation)
        
        val contrastMatrix = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, (1f - contrast) / 2f * 255f,
            0f, contrast, 0f, 0f, (1f - contrast) / 2f * 255f,
            0f, 0f, contrast, 0f, (1f - contrast) / 2f * 255f,
            0f, 0f, 0f, 1f, 0f
        ))
        
        colorMatrix.postConcat(contrastMatrix)
        return colorMatrix
    }
    
    /**
     * CLASSIC: Vintage film look with sepia tones
     */
    private fun createClassicMatrix(strength: Float): ColorMatrix {
        val colorMatrix = ColorMatrix()
        
        // Sepia effect
        val sepiaMatrix = ColorMatrix(floatArrayOf(
            0.393f * strength + (1f - strength), 0.769f * strength, 0.189f * strength, 0f, 0f,
            0.349f * strength, 0.686f * strength + (1f - strength), 0.168f * strength, 0f, 0f,
            0.272f * strength, 0.534f * strength, 0.131f * strength + (1f - strength), 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        
        colorMatrix.set(sepiaMatrix)
        return colorMatrix
    }
    
    /**
     * CONTEMPORARY: Modern look with enhanced clarity
     */
    private fun createContemporaryMatrix(strength: Float): ColorMatrix {
        val colorMatrix = ColorMatrix()
        
        // Boost blue and reduce yellow for modern digital look
        val modernMatrix = ColorMatrix(floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f + (0.1f * strength), 0f, 0f, 0f,
            0f, 0f, 1f + (0.2f * strength), 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        
        // Slight saturation boost
        val saturation = 1f + (0.2f * strength)
        colorMatrix.setSaturation(saturation)
        colorMatrix.postConcat(modernMatrix)
        
        return colorMatrix
    }
    
    /**
     * Get color overlay for preview (lighter version of the filter)
     */
    fun getPreviewOverlayColor(filmEffect: FilmEffect, strength: Float): Color {
        val alpha = (strength / 100f * 0.1f).coerceIn(0f, 0.15f)
        
        return when (filmEffect.shortName) {
            "VIV" -> Color.Cyan.copy(alpha = alpha)
            "NAT" -> Color.Green.copy(alpha = alpha)
            "CHR" -> Color.Gray.copy(alpha = alpha)
            "CLS" -> Color(0xFFD2691E).copy(alpha = alpha) // Orange-brown for sepia
            "CNT" -> Color.Blue.copy(alpha = alpha)
            else -> Color.Transparent
        }
    }
}

/**
 * Extension functions for easier filter application
 */
fun Bitmap.applyFilmEffect(effect: FilmEffect, strength: Float): Bitmap {
    return FilterProcessor.applyFilter(this, effect, strength)
}

fun FilmEffect.getPreviewOverlay(strength: Float): Color {
    return FilterProcessor.getPreviewOverlayColor(this, strength)
}