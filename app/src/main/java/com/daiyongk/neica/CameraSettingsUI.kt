package com.daiyongk.neica

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Settings dialog that appears when clicking on a camera setting
 */
@Composable
fun CameraSettingsDialog(
    setting: CameraSetting,
    settingsManager: CameraSettingsManager,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = setting.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Setting-specific content
                when (setting.name) {
                    "Format" -> FileFormatSettings(settingsManager)
                    "Embed Location" -> LocationSettings(settingsManager)
                    "Flash" -> FlashSettings(settingsManager)
                    "Timer" -> TimerSettings(settingsManager)
                    "Grid" -> GridSettings(settingsManager)
                    "Level" -> LevelSettings(settingsManager)
                    "Focus Peaking" -> FocusPeakingSettings(settingsManager)
                    "Histogram" -> HistogramSettings(settingsManager)
                }
            }
        }
    }
}

@Composable
fun FileFormatSettings(settingsManager: CameraSettingsManager) {
    val currentFormat by settingsManager.fileFormat
    
    Column {
        Text(
            text = "Select file format for captured photos",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        FileFormat.values().forEach { format ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (currentFormat == format) Color.White.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .clickable { settingsManager.setFileFormat(format) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentFormat == format,
                    onClick = { settingsManager.setFileFormat(format) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = format.displayName,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    if (format == FileFormat.RAW || format == FileFormat.JPEG_RAW) {
                        Text(
                            text = "Professional format with more editing flexibility",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationSettings(settingsManager: CameraSettingsManager) {
    val embedLocation by settingsManager.embedLocation
    
    Column {
        Text(
            text = "Embed GPS location data in photo metadata",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Embed Location",
                color = Color.White,
                fontSize = 16.sp
            )
            Switch(
                checked = embedLocation,
                onCheckedChange = { settingsManager.setEmbedLocation(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Green
                )
            )
        }
        
        if (embedLocation) {
            Text(
                text = "⚠️ Location permission required",
                color = Color.Yellow,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun FlashSettings(settingsManager: CameraSettingsManager) {
    val currentMode by settingsManager.flashMode
    
    Column {
        Text(
            text = "Control camera flash behavior",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        FlashMode.values().forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (currentMode == mode) Color.White.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .clickable { settingsManager.setFlashMode(mode) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentMode == mode,
                    onClick = { settingsManager.setFlashMode(mode) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when(mode) {
                            FlashMode.OFF -> Icons.Default.Close  // Using Close as substitute for FlashOff
                            FlashMode.ON -> Icons.Default.Check   // Using Check as substitute for FlashOn
                            FlashMode.AUTO -> Icons.Default.Star   // Using Star as substitute for FlashAuto
                        },
                        contentDescription = mode.displayName,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mode.displayName,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TimerSettings(settingsManager: CameraSettingsManager) {
    val currentDuration by settingsManager.timerDuration
    
    Column {
        Text(
            text = "Set self-timer delay",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TimerDuration.values().forEach { duration ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clickable { settingsManager.setTimerDuration(duration) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentDuration == duration) 
                            Color.White.copy(alpha = 0.3f) 
                        else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.DateRange,  // Using DateRange as substitute for Timer
                                contentDescription = duration.displayName,
                                tint = if (currentDuration == duration) Color.White else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = duration.displayName,
                                color = if (currentDuration == duration) Color.White else Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = if (currentDuration == duration) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridSettings(settingsManager: CameraSettingsManager) {
    val showGrid by settingsManager.showGrid
    
    Column {
        Text(
            text = "Display composition grid overlay",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Menu,  // Using Menu as substitute for Grid3x3
                    contentDescription = "Grid",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Show Grid",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Switch(
                checked = showGrid,
                onCheckedChange = { settingsManager.toggleGrid() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Green
                )
            )
        }
        
        Text(
            text = "Helps with rule of thirds composition",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun LevelSettings(settingsManager: CameraSettingsManager) {
    val showLevel by settingsManager.showLevel
    
    Column {
        Text(
            text = "Display horizon level indicator",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MoreVert,  // Using MoreVert as substitute for HorizontalRule
                    contentDescription = "Level",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Show Level",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Switch(
                checked = showLevel,
                onCheckedChange = { settingsManager.toggleLevel() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Green
                )
            )
        }
        
        Text(
            text = "Uses device gyroscope to show camera tilt",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun FocusPeakingSettings(settingsManager: CameraSettingsManager) {
    val focusPeaking by settingsManager.focusPeaking
    
    Column {
        ProFeatureBadge()
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Highlights in-focus areas with colored overlay",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enable Focus Peaking",
                color = Color.White,
                fontSize = 16.sp
            )
            Switch(
                checked = focusPeaking,
                onCheckedChange = { settingsManager.toggleFocusPeaking() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Green
                )
            )
        }
        
        if (focusPeaking) {
            Text(
                text = "In-focus areas will be highlighted in red",
                color = Color.Red.copy(alpha = 0.8f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun HistogramSettings(settingsManager: CameraSettingsManager) {
    val showHistogram by settingsManager.showHistogram
    
    Column {
        ProFeatureBadge()
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Display real-time exposure histogram",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show Histogram",
                color = Color.White,
                fontSize = 16.sp
            )
            Switch(
                checked = showHistogram,
                onCheckedChange = { settingsManager.toggleHistogram() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.Green
                )
            )
        }
        
        if (showHistogram) {
            Text(
                text = "RGB histogram will appear in top-right corner",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun ProFeatureBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Red)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "PRO FEATURE",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}