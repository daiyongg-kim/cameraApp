package com.daiyongk.neica

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos

/**
 * Grid overlay for rule of thirds composition
 */
@Composable
fun GridOverlay(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = 1.dp.toPx()
        val color = Color.White.copy(alpha = 0.3f)
        
        // Draw vertical lines
        val verticalSpacing = size.width / 3
        for (i in 1..2) {
            drawLine(
                color = color,
                start = Offset(verticalSpacing * i, 0f),
                end = Offset(verticalSpacing * i, size.height),
                strokeWidth = strokeWidth
            )
        }
        
        // Draw horizontal lines
        val horizontalSpacing = size.height / 3
        for (i in 1..2) {
            drawLine(
                color = color,
                start = Offset(0f, horizontalSpacing * i),
                end = Offset(size.width, horizontalSpacing * i),
                strokeWidth = strokeWidth
            )
        }
    }
}

/**
 * Level indicator showing device tilt
 */
@Composable
fun LevelIndicator(
    tiltAngle: Float, // Device tilt in degrees (-90 to 90)
    modifier: Modifier = Modifier
) {
    val isLevel = abs(tiltAngle) < 2f // Consider level within 2 degrees
    val indicatorColor = if (isLevel) Color.Green else Color.Yellow
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(40.dp)
        ) {
            val centerY = size.height / 2
            val centerX = size.width / 2
            
            // Draw horizon line
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
            )
            
            // Draw center marker
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = 3.dp.toPx(),
                center = Offset(centerX, centerY),
                style = Stroke(width = 1.dp.toPx())
            )
            
            // Draw tilt indicator
            val tiltOffset = (tiltAngle / 90f) * (size.width / 2)
            val indicatorX = centerX + tiltOffset
            
            // Draw bubble
            drawCircle(
                color = indicatorColor,
                radius = 6.dp.toPx(),
                center = Offset(indicatorX.coerceIn(10f, size.width - 10f), centerY)
            )
            
            // Draw angle text
            if (!isLevel) {
                // This would need a separate Text composable overlay
            }
        }
        
        // Display angle text
        if (!isLevel) {
            Text(
                text = "${tiltAngle.toInt()}°",
                color = indicatorColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
            )
        }
    }
}

/**
 * Histogram display for exposure analysis
 */
@Composable
fun HistogramOverlay(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    if (bitmap == null) return
    
    val histogramData = remember(bitmap) {
        calculateHistogram(bitmap)
    }
    
    Box(
        modifier = modifier
            .size(120.dp, 80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawHistogram(histogramData)
        }
    }
}

private fun calculateHistogram(bitmap: Bitmap): HistogramData {
    val red = IntArray(256)
    val green = IntArray(256)
    val blue = IntArray(256)
    
    // Sample pixels for performance (every 10th pixel)
    val step = 10
    for (x in 0 until bitmap.width step step) {
        for (y in 0 until bitmap.height step step) {
            val pixel = bitmap.getPixel(x, y)
            red[AndroidColor.red(pixel)]++
            green[AndroidColor.green(pixel)]++
            blue[AndroidColor.blue(pixel)]++
        }
    }
    
    // Find max value for normalization
    val maxValue = (red + green + blue).maxOrNull() ?: 1
    
    return HistogramData(red, green, blue, maxValue)
}

private fun DrawScope.drawHistogram(data: HistogramData) {
    val width = size.width
    val height = size.height
    val bucketWidth = width / 256f
    
    // Draw RGB channels
    for (i in 0..255) {
        val x = i * bucketWidth
        
        // Red channel
        val redHeight = (data.red[i].toFloat() / data.maxValue) * height
        drawLine(
            color = Color.Red.copy(alpha = 0.5f),
            start = Offset(x, height),
            end = Offset(x, height - redHeight),
            strokeWidth = bucketWidth
        )
        
        // Green channel
        val greenHeight = (data.green[i].toFloat() / data.maxValue) * height
        drawLine(
            color = Color.Green.copy(alpha = 0.5f),
            start = Offset(x, height),
            end = Offset(x, height - greenHeight),
            strokeWidth = bucketWidth
        )
        
        // Blue channel
        val blueHeight = (data.blue[i].toFloat() / data.maxValue) * height
        drawLine(
            color = Color.Blue.copy(alpha = 0.5f),
            start = Offset(x, height),
            end = Offset(x, height - blueHeight),
            strokeWidth = bucketWidth
        )
    }
}

/**
 * Focus peaking overlay highlighting in-focus areas
 */
@Composable
fun FocusPeakingOverlay(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    if (bitmap == null) return
    
    val edgeMap = remember(bitmap) {
        detectEdges(bitmap)
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw red overlay on high-contrast edges (in-focus areas)
        edgeMap.forEach { edge ->
            drawCircle(
                color = Color.Red.copy(alpha = 0.6f),
                radius = 1.dp.toPx(),
                center = Offset(
                    edge.x * (size.width / bitmap.width),
                    edge.y * (size.height / bitmap.height)
                )
            )
        }
    }
}

/**
 * Timer countdown overlay
 */
@Composable
fun TimerCountdownOverlay(
    secondsRemaining: Int,
    modifier: Modifier = Modifier
) {
    if (secondsRemaining <= 0) return
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = secondsRemaining.toString(),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Helper data classes
private data class HistogramData(
    val red: IntArray,
    val green: IntArray,
    val blue: IntArray,
    val maxValue: Int
)

private data class EdgePoint(val x: Int, val y: Int)

// Simple edge detection for focus peaking (Sobel operator)
private fun detectEdges(bitmap: Bitmap): List<EdgePoint> {
    val edges = mutableListOf<EdgePoint>()
    val threshold = 100 // Adjust for sensitivity
    
    // Sample for performance
    val step = 5
    for (x in 1 until bitmap.width - 1 step step) {
        for (y in 1 until bitmap.height - 1 step step) {
            // Simplified Sobel edge detection
            val gx = getGradientX(bitmap, x, y)
            val gy = getGradientY(bitmap, x, y)
            val magnitude = kotlin.math.sqrt((gx * gx + gy * gy).toDouble()).toInt()
            
            if (magnitude > threshold) {
                edges.add(EdgePoint(x, y))
            }
        }
    }
    
    return edges
}

private fun getGradientX(bitmap: Bitmap, x: Int, y: Int): Int {
    // Sobel X kernel
    val left = getLuminance(bitmap.getPixel(x - 1, y))
    val right = getLuminance(bitmap.getPixel(x + 1, y))
    return right - left
}

private fun getGradientY(bitmap: Bitmap, x: Int, y: Int): Int {
    // Sobel Y kernel
    val top = getLuminance(bitmap.getPixel(x, y - 1))
    val bottom = getLuminance(bitmap.getPixel(x, y + 1))
    return bottom - top
}

private fun getLuminance(pixel: Int): Int {
    val r = AndroidColor.red(pixel)
    val g = AndroidColor.green(pixel)
    val b = AndroidColor.blue(pixel)
    return (0.299 * r + 0.587 * g + 0.114 * b).toInt()
}