package com.daiyongk.neica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Custom filter adjustment dialog for advanced users
 */
@Composable
fun CustomFilterDialog(
    filmEffect: FilmEffect,
    onDismiss: () -> Unit,
    onApplyCustomFilter: (CustomFilterSettings) -> Unit
) {
    val saturation = remember { mutableFloatStateOf(100f) }
    val contrast = remember { mutableFloatStateOf(100f) }
    val brightness = remember { mutableFloatStateOf(100f) }
    val warmth = remember { mutableFloatStateOf(100f) }
    val vignette = remember { mutableFloatStateOf(0f) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Custom ${filmEffect.name}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Saturation
                FilterSlider(
                    label = "Saturation",
                    value = saturation.floatValue,
                    onValueChange = { saturation.floatValue = it },
                    color = Color.Red
                )
                
                // Contrast
                FilterSlider(
                    label = "Contrast",
                    value = contrast.floatValue,
                    onValueChange = { contrast.floatValue = it },
                    color = Color.Yellow
                )
                
                // Brightness
                FilterSlider(
                    label = "Brightness",
                    value = brightness.floatValue,
                    onValueChange = { brightness.floatValue = it },
                    color = Color.White
                )
                
                // Warmth
                FilterSlider(
                    label = "Warmth",
                    value = warmth.floatValue,
                    onValueChange = { warmth.floatValue = it },
                    color = Color(0xFFFF8C00) // Dark orange
                )
                
                // Vignette
                FilterSlider(
                    label = "Vignette",
                    value = vignette.floatValue,
                    onValueChange = { vignette.floatValue = it },
                    color = Color.Gray,
                    range = 0f..100f
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.White)
                    }
                    
                    TextButton(
                        onClick = {
                            val customSettings = CustomFilterSettings(
                                saturation = saturation.floatValue,
                                contrast = contrast.floatValue,
                                brightness = brightness.floatValue,
                                warmth = warmth.floatValue,
                                vignette = vignette.floatValue
                            )
                            onApplyCustomFilter(customSettings)
                            onDismiss()
                        }
                    ) {
                        Text("Apply", color = filmEffect.primaryColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    range: ClosedFloatingPointRange<Float> = 0f..200f
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = "${value.toInt()}%",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.3f)
            )
        )
    }
}

/**
 * Custom filter settings data class
 */
data class CustomFilterSettings(
    val saturation: Float = 100f,
    val contrast: Float = 100f,
    val brightness: Float = 100f,
    val warmth: Float = 100f,
    val vignette: Float = 0f
)