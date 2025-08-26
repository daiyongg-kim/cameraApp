package com.daiyongk.neica

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileOutputStream
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.daiyongk.neica.ui.theme.CameraAppTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.io.File

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("CameraApp", "Camera permission granted")
        } else {
            Log.d("CameraApp", "Camera permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check and request camera permission
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                Log.d("CameraApp", "Camera permission already granted")
            }
            else -> {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        
        setContent {
            CameraAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CameraPreviewScreen()
                }
            }
        }
    }
}

@Composable
fun CameraPreviewScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraSelector = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val scope = rememberCoroutineScope()
    
    // Initialize settings manager
    val settingsManager = remember { CameraSettingsManager(context) }
    
    // Configure ImageCapture with flash mode from settings
    val imageCapture = remember { 
        ImageCapture.Builder()
            .setFlashMode(settingsManager.getCameraXFlashMode())
            .build() 
    }
    val previewView = remember { PreviewView(context) }
    
    // Update flash mode when it changes
    LaunchedEffect(settingsManager.flashMode.value) {
        imageCapture.flashMode = settingsManager.getCameraXFlashMode()
    }
    
    // Film effect states
    val filmEffects = remember { FilmEffects.getAllEffects() }
    val selectedEffect = remember { mutableStateOf(FilmEffects.CHR) }
    val effectStrength = remember { mutableFloatStateOf(100f) }
    
    // Camera mode states
    val currentMode = remember { mutableStateOf<CameraMode>(CameraMode.Photo) }
    val showModeSelector = remember { mutableStateOf(false) }
    
    // Gallery states
    val latestPhotoPath = remember { mutableStateOf<String?>(null) }
    val showPhotoViewer = remember { mutableStateOf(false) }
    val allPhotos = remember { mutableStateOf<List<File>>(emptyList()) }
    
    // Settings dialog state
    val showSettingsDialog = remember { mutableStateOf<CameraSetting?>(null) }
    
    // Timer state
    val timerCountdown = remember { mutableStateOf(0) }
    
    // Device tilt for level indicator (would need sensor integration)
    val deviceTilt = remember { mutableStateOf(0f) }
    
    // Load all photos on launch
    LaunchedEffect(Unit) {
        allPhotos.value = loadAllPhotos(context)
        if (allPhotos.value.isNotEmpty()) {
            latestPhotoPath.value = allPhotos.value.last().absolutePath
        }
    }
    
    // Check if camera permission is granted
    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(cameraSelector.value, hasCameraPermission) {
        if (!hasCameraPermission) {
            Log.d("CameraApp", "Camera permission not granted, skipping camera initialization")
            return@LaunchedEffect
        }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = CameraPreview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector.value,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraApp", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Camera preview
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        
        // Filter overlay for visual preview
        CameraFilterOverlay(
            filmEffect = selectedEffect.value,
            strength = effectStrength.floatValue,
            modifier = Modifier.fillMaxSize()
        )
        
        // Grid overlay
        if (settingsManager.showGrid.value) {
            GridOverlay(modifier = Modifier.fillMaxSize())
        }
        
        // Level indicator
        if (settingsManager.showLevel.value) {
            LevelIndicator(
                tiltAngle = deviceTilt.value,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 100.dp)
            )
        }
        
        // Histogram overlay (Pro feature)
        if (settingsManager.showHistogram.value) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 16.dp)
            ) {
                // Would need to capture preview bitmap for histogram
                // HistogramOverlay(bitmap = previewBitmap)
            }
        }
        
        // Timer countdown overlay
        if (timerCountdown.value > 0) {
            TimerCountdownOverlay(
                secondsRemaining = timerCountdown.value,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Camera switch button (top right)
        IconButton(
            onClick = {
                cameraSelector.value = if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 50.dp, end = 50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera_switch),
                contentDescription = "Switch Camera",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        // Top overlay with effect name
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
        ) {
            Text(
                text = "${selectedEffect.value.name} ${effectStrength.floatValue.toInt()}%",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Film effect selector - circular scrolling
        CircularFilmEffectSelector(
            effects = filmEffects,
            selectedEffect = selectedEffect.value,
            onEffectSelected = { selectedEffect.value = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 200.dp)
                .fillMaxWidth(),
            visibleItemsCount = 5
        )

        // Strength slider
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 140.dp)
        ) {
            Text(
                text = "Strength",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "${effectStrength.floatValue.toInt()}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Slider(
                value = effectStrength.floatValue,
                onValueChange = { effectStrength.floatValue = it },
                valueRange = 0f..100f,
                modifier = Modifier.width(200.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }

        // Camera controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery thumbnail
            GalleryThumbnail(
                photoPath = latestPhotoPath.value,
                onClick = { 
                    if (allPhotos.value.isNotEmpty()) {
                        showPhotoViewer.value = true
                    } else {
                        Toast.makeText(context, "No photos taken yet", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            // Capture button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { 
                        scope.launch {
                            takePhotoWithTimer(
                                imageCapture = imageCapture,
                                context = context,
                                filmEffect = selectedEffect.value,
                                strength = effectStrength.floatValue,
                                settingsManager = settingsManager,
                                onCountdown = { seconds ->
                                    timerCountdown.value = seconds
                                },
                                onPhotoSaved = { photoPath ->
                                    latestPhotoPath.value = photoPath
                                    // Refresh photo list
                                    allPhotos.value = loadAllPhotos(context)
                                }
                            )
                        }
                    }
            )

            // Camera mode selector button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { showModeSelector.value = !showModeSelector.value },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentMode.value.shortName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Camera Mode Selector Overlay
        if (showModeSelector.value) {
            CameraModeSelector(
                currentMode = currentMode.value,
                onModeSelected = { mode ->
                    currentMode.value = mode
                    showModeSelector.value = false
                },
                onDismiss = { showModeSelector.value = false },
                onSettingClick = { setting ->
                    showSettingsDialog.value = setting
                    showModeSelector.value = false
                }
            )
        }
        
        // Settings Dialog
        showSettingsDialog.value?.let { setting ->
            CameraSettingsDialog(
                setting = setting,
                settingsManager = settingsManager,
                onDismiss = { showSettingsDialog.value = null }
            )
        }
        
        // Photo Viewer Overlay
        if (showPhotoViewer.value) {
            PhotoViewer(
                photos = allPhotos.value,
                onDismiss = { showPhotoViewer.value = false },
                onPhotoDeleted = { deletedPhoto ->
                    // Refresh photo list after deletion
                    allPhotos.value = loadAllPhotos(context)
                    // Update latest photo thumbnail
                    if (allPhotos.value.isNotEmpty()) {
                        latestPhotoPath.value = allPhotos.value.last().absolutePath
                    } else {
                        latestPhotoPath.value = null
                        showPhotoViewer.value = false // Close viewer if no photos left
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoViewer(
    photos: List<File>,
    onDismiss: () -> Unit,
    onPhotoDeleted: (File) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = photos.size - 1, // Start with latest photo
        pageCount = { photos.size }
    )
    
    val showDeleteDialog = remember { mutableStateOf(false) }
    val photoToDelete = remember { mutableStateOf<File?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { onDismiss() }
            }
    ) {
        if (photos.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val photoFile = photos[page]
                PhotoImage(photoFile = photoFile, page = page)
            }
            
            // Photo counter
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${photos.size}",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
        
        // Top controls row
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Delete button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.Red.copy(alpha = 0.8f),
                        CircleShape
                    )
                    .clickable { 
                        if (photos.isNotEmpty()) {
                            photoToDelete.value = photos[pagerState.currentPage]
                            showDeleteDialog.value = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete Photo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Close button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        CircleShape
                    )
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "×",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Delete confirmation dialog
        if (showDeleteDialog.value && photoToDelete.value != null) {
            DeletePhotoDialog(
                onConfirm = {
                    photoToDelete.value?.let { file ->
                        if (file.delete()) {
                            onPhotoDeleted(file)
                        }
                    }
                    showDeleteDialog.value = false
                    photoToDelete.value = null
                },
                onDismiss = {
                    showDeleteDialog.value = false
                    photoToDelete.value = null
                }
            )
        }
    }
}

@Composable
fun DeletePhotoDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Photo",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to delete this photo? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = "Delete",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

fun loadAllPhotos(context: Context): List<File> {
    val photosDir = File(context.filesDir, "photos")
    return if (photosDir.exists() && photosDir.isDirectory) {
        photosDir.listFiles()?.filter { it.isFile && it.extension.lowercase() == "jpg" }?.sortedBy { it.lastModified() } ?: emptyList()
    } else {
        emptyList()
    }
}

@Composable
fun CameraModeSelector(
    currentMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit,
    onDismiss: () -> Unit,
    onSettingClick: (CameraSetting) -> Unit
) {
    // Semi-transparent overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() }
    ) {
        // Mode selector panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Color.Black.copy(alpha = 0.9f),
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(24.dp)
        ) {
            // Mode selector buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Photo Mode
                ModeButton(
                    mode = CameraMode.Photo,
                    isSelected = currentMode == CameraMode.Photo,
                    onClick = { onModeSelected(CameraMode.Photo) }
                )
                
                // Aperture Mode
                ModeButton(
                    mode = CameraMode.Aperture,
                    isSelected = currentMode == CameraMode.Aperture,
                    onClick = { onModeSelected(CameraMode.Aperture) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Camera settings grid
            val settings = CameraSettings.getAllSettings()
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First row of settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    settings.take(4).forEach { setting ->
                        CameraSettingButton(
                            setting = setting,
                            onClick = { onSettingClick(setting) }
                        )
                    }
                }
                
                // Second row of settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    settings.drop(4).forEach { setting ->
                        CameraSettingButton(
                            setting = setting,
                            onClick = { onSettingClick(setting) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bottom controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Film simulation strength
                Text(
                    text = "100%",
                    color = Color.White,
                    fontSize = 14.sp
                )
                
                // Menu button
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Menu",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_legacy),
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModeButton(
    mode: CameraMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color.White.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mode.displayName,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun CameraSettingButton(
    setting: CameraSetting,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        // Setting icon placeholder
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Add PRO badge if needed
            if (setting.hasProBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Red, CircleShape)
                        .padding(2.dp)
                ) {
                    Text(
                        text = "PRO",
                        color = Color.White,
                        fontSize = 6.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = setting.name,
            color = Color.White,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GalleryThumbnail(
    photoPath: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (photoPath != null && File(photoPath).exists()) {
            // Load and display the thumbnail
            val bitmap = remember(photoPath) { 
                try {
                    BitmapFactory.decodeFile(photoPath)
                } catch (e: Exception) {
                    null
                }
            }
            
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Latest Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback icon
                Text(
                    text = "📷",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        } else {
            // No photo taken yet, show camera icon
            Text(
                text = "📷",
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun PhotoImage(
    photoFile: File,
    page: Int
) {
    val bitmap = remember(photoFile.absolutePath) {
        try {
            BitmapFactory.decodeFile(photoFile.absolutePath)
        } catch (e: Exception) {
            null
        }
    }
    
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Photo ${page + 1}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    } else {
        // Error loading image
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Error loading image",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun FilmEffectButton(
    effect: FilmEffect,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = effect.shortName,
            color = if (isSelected) effect.primaryColor else Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

private suspend fun takePhotoWithTimer(
    imageCapture: ImageCapture, 
    context: Context, 
    filmEffect: FilmEffect, 
    strength: Float,
    settingsManager: CameraSettingsManager,
    onCountdown: (Int) -> Unit = {},
    onPhotoSaved: (String) -> Unit = {}
) {
    // Handle timer countdown
    val timerDuration = settingsManager.timerDuration.value
    if (timerDuration != TimerDuration.OFF) {
        for (i in timerDuration.seconds downTo 1) {
            onCountdown(i)
            delay(1000)
        }
        onCountdown(0)
    }
    
    takePhoto(imageCapture, context, filmEffect, strength, onPhotoSaved)
}

private fun takePhoto(
    imageCapture: ImageCapture, 
    context: Context, 
    filmEffect: FilmEffect, 
    strength: Float,
    onPhotoSaved: (String) -> Unit = {}
) {
    // Use internal app storage which doesn't require external storage permissions
    val photoFile = File(
        context.filesDir,
        "photos/${System.currentTimeMillis()}_${filmEffect.shortName}_${strength.toInt()}.jpg"
    )
    
    // Create the photos directory if it doesn't exist
    photoFile.parentFile?.mkdirs()

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Apply filter to the captured image
                try {
                    applyFilterToSavedPhoto(photoFile, filmEffect, strength)
                    Log.d("CameraApp", "Photo captured and filtered with ${filmEffect.name} at ${strength.toInt()}%: ${photoFile.absolutePath}")
                    Toast.makeText(
                        context, 
                        "Photo saved with ${filmEffect.shortName} ${strength.toInt()}%", 
                        Toast.LENGTH_SHORT
                    ).show()
                    onPhotoSaved(photoFile.absolutePath)
                } catch (e: Exception) {
                    Log.e("CameraApp", "Failed to apply filter to photo", e)
                    Toast.makeText(context, "Photo saved but filter failed to apply", Toast.LENGTH_SHORT).show()
                    onPhotoSaved(photoFile.absolutePath)
                }
            }

            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraApp", "Photo capture failed: ${exc.message}", exc)
                Toast.makeText(context, "Photo capture failed: ${exc.message}", Toast.LENGTH_LONG).show()
            }
        }
    )
}

/**
 * Apply filter to saved photo
 */
private fun applyFilterToSavedPhoto(photoFile: File, filmEffect: FilmEffect, strength: Float) {
    try {
        // Load the original bitmap
        val originalBitmap = BitmapFactory.decodeFile(photoFile.absolutePath) ?: return
        
        // Apply the filter
        val filteredBitmap = originalBitmap.applyFilmEffect(filmEffect, strength)
        
        // Save the filtered bitmap back to the same file
        FileOutputStream(photoFile).use { out ->
            filteredBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
        
        // Clean up
        if (originalBitmap != filteredBitmap) {
            originalBitmap.recycle()
        }
        filteredBitmap.recycle()
        
    } catch (e: Exception) {
        Log.e("CameraApp", "Error applying filter to photo: ${e.message}", e)
        throw e
    }
}

// Legacy function for compatibility
private fun takePhoto(imageCapture: ImageCapture, context: Context) {
    takePhoto(imageCapture, context, FilmEffects.NAT, 100f)
}

@Preview(showBackground = true)
@Composable
fun CameraPreviewScreenPreview() {
    CameraAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
             Button(
                onClick = { },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Take Picture")
            }
             Button(
                onClick = { },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Switch")
            }
        }
    }
}
