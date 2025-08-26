# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NEICA is an Android camera app built with Jetpack Compose and CameraX. It provides a minimal interface for capturing photos with film-style effects.

## Build & Development Commands

### Build the project
```bash
./gradlew build
```

### Run on device/emulator
```bash
./gradlew installDebug
# or
./gradlew assembleDebug
```

### Clean build
```bash
./gradlew clean
```

### Run tests
```bash
./gradlew test
./gradlew connectedAndroidTest  # for instrumented tests
```

### Lint check
```bash
./gradlew lint
```

## Architecture & Core Components

### Package Structure
- **Base Package**: `com.daiyongk.neica`
- **Namespace**: `com.daiyongk.neica`
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Core Components Architecture

#### MainActivity
Entry point that handles:
- Camera permission requests via `ActivityResultContracts`
- CameraX lifecycle management with `ProcessCameraProvider`
- Image capture coordination between preview and storage
- UI state management for camera modes and film effects

#### Film Effects System
**FilmEffect.kt**: Data model for film presets
- 5 predefined effects: VIVID, NATURAL, CHROME, CLASSIC, CONTEMPORARY
- Each effect has name, shortName, description, and primaryColor properties

**FilterProcessor.kt**: Core image processing engine
- Applies ColorMatrix transformations based on film effect type
- Each effect modifies saturation, contrast, and color channels differently:
  - VIVID: Enhanced saturation + contrast
  - NATURAL: Balanced with subtle warm tones
  - CHROME: High contrast + desaturation for metallic look
  - CLASSIC: Sepia tones for vintage aesthetic
  - CONTEMPORARY: Blue boost + enhanced clarity
- Provides preview overlay colors for real-time camera preview

#### Camera Modes
**CameraMode.kt**: Defines camera operation modes
- Photo mode: Standard capture
- Aperture mode: Priority mode for depth control (UI only, not yet implemented)
- Settings system with pro features flagged (e.g., Focus Peaking, Histogram)

#### Camera Settings System (NEW)
**CameraSettingsManager.kt**: Centralized settings management with persistence
- File Format: JPEG, RAW (DNG), or JPEG+RAW
- Location Embedding: GPS data in EXIF metadata
- Flash Control: Off, On, Auto modes
- Timer: Off, 3s, 10s countdown
- Grid Overlay: Rule of thirds composition guide
- Level Indicator: Horizon alignment using device gyroscope
- Focus Peaking (Pro): Highlights in-focus areas
- Histogram (Pro): Real-time exposure analysis

**CameraSettingsUI.kt**: Settings dialog components
- Individual setting dialogs for each feature
- Switch controls for toggles
- Radio buttons for exclusive options
- Pro feature badges for premium features

**CameraOverlays.kt**: Visual overlay components
- GridOverlay: 3x3 composition grid
- LevelIndicator: Tilt angle display with bubble indicator
- HistogramOverlay: RGB histogram visualization
- FocusPeakingOverlay: Edge detection for focus areas
- TimerCountdownOverlay: Large countdown display

#### UI Components
- **CameraFilterOverlay**: Real-time filter preview layer
- **CircularScrollingSelector**: Film effect selector with circular scrolling
- **CustomFilterDialog**: Manual filter adjustment interface

### Technology Stack
- **UI Framework**: Jetpack Compose with Material3
- **Camera**: CameraX (camera2, lifecycle, view)
- **Build System**: Gradle 9.0 with Kotlin DSL
- **Language**: Kotlin with JVM target 1.8

### Key Patterns
1. **State Management**: Compose remember state for UI reactivity
2. **Permission Handling**: ActivityResultContracts for modern permission flow
3. **Image Processing**: Bitmap manipulation with Canvas and ColorMatrix
4. **Camera Integration**: CameraX with lifecycle-aware components

## Important Implementation Details

### Camera Permission
The app uses runtime permission model. Camera permission is requested on startup and handled via `registerForActivityResult`.

### Image Capture Flow
1. User taps capture button
2. Timer countdown (if enabled) starts
3. Flash fires (if enabled) during capture
4. ImageCapture use case captures photo
5. FilterProcessor applies selected film effect
6. Location data embedded (if enabled and permitted)
7. File saved in selected format (JPEG/RAW/Both)
8. Gallery viewer can display captured photos

### Filter Processing
Filters work by manipulating ColorMatrix values. Each film effect has a unique matrix transformation that adjusts RGB channels, saturation, and contrast. The strength parameter (0-100) controls the intensity of the effect.

## Development Notes

- The app uses version catalogs (libs.versions.toml) for dependency management
- Compose compiler version is explicitly set to 1.5.1
- ProGuard is disabled for release builds (isMinifyEnabled = false)
- The app requires physical camera hardware (android.hardware.camera.any)

## Critical Issues & Maintenance Tasks

### High Priority Issues

#### Memory Management
- **FilterProcessor.kt:24**: Bitmap memory leak - creates new bitmap without disposing original
- **MainActivity.kt:873-896**: Full-resolution bitmap processing on main thread causes UI freezing
- **MainActivity.kt:732-739**: Gallery loads full-size images for thumbnails

#### Recently Implemented Features ✅
- **Camera Settings**: All 8 settings now functional with dialogs and persistence
  - Format selection (JPEG/RAW/Both)
  - Location embedding with GPS permissions
  - Flash control (Off/On/Auto)
  - Timer (3s/10s countdown)
  - Grid overlay for composition
  - Level indicator for alignment
  - Focus Peaking (Pro feature)
  - Histogram display (Pro feature)
- **Settings Persistence**: SharedPreferences for all settings
- **Visual Overlays**: Grid, Level, Histogram, Timer countdown
- **Build Status**: Successfully compiling with Material3 icon compatibility fixes

#### Still Unimplemented Features
- **Aperture Mode**: Defined in CameraMode.kt:11 but functionality not implemented
- **Custom Filter Dialog**: CustomFilterDialog.kt exists but isn't integrated into UI
- **RAW File Support**: UI allows selection but actual DNG capture not implemented
- **Location Embedding**: Permission requested but GPS data not actually embedded in EXIF
- **Focus Peaking**: Overlay created but edge detection needs camera preview access
- **Histogram**: Component created but needs live preview bitmap access

#### Error Handling Gaps
- **MainActivity.kt:855-859**: Filter application failures only log to console, no user feedback
- **MainActivity.kt:87-93**: Permission denial not handled properly in UI
- **MainActivity.kt:528-535**: No error handling for file I/O operations

### Architecture Concerns

#### Code Organization
- **MainActivity.kt**: 927 lines - needs refactoring into MVVM pattern with ViewModels
- **Missing Dependency Injection**: Consider adding Hilt or Koin
- **No Repository Pattern**: Direct file system access in UI layer

#### Testing
- **Zero test coverage**: No unit tests, integration tests, or UI tests exist
- **Missing test infrastructure**: Need to add Mockk, Espresso, Camera2 testing

### Performance Bottlenecks
- **Main thread bitmap operations**: Move to coroutines with IO dispatcher
- **CircularScrollingSelector.kt:50-58**: Creates 1001 items in memory for carousel
- **Image loading**: No bitmap scaling or caching for thumbnails

### Security Considerations
- **Missing permissions**: Need READ/WRITE_EXTERNAL_STORAGE for proper photo storage
- **build.gradle.kts:25**: ProGuard disabled (`isMinifyEnabled = false`)
- **File path security**: Paths constructed without sanitization

### Hardcoded Values to Configure
- **CircularScrollingSelector.kt**: UI dimensions (140.dp padding, 72.dp width)
- **FilterProcessor.kt**: Filter strength multipliers (0.5f saturation, 0.3f contrast)
- **MainActivity.kt:832**: File naming pattern

## Recommended Development Workflow

### Before Starting New Features
1. Check memory management issues in FilterProcessor and MainActivity
2. Verify bitmap operations are moved off main thread
3. Review error handling for the feature area

### When Adding Features
1. Follow MVVM pattern - avoid adding logic to MainActivity
2. Add comprehensive error handling with user feedback
3. Include unit tests for new functionality
4. Use coroutines for async operations

### Testing Checklist
- Test with low memory devices
- Verify permission handling flows
- Check filter application with various image sizes
- Test gallery performance with many photos

### Priority Fix Order
1. **Immediate**: Fix bitmap memory leaks, move processing off main thread
2. **High**: Implement Aperture mode, add error handling UI
3. **Medium**: Refactor to MVVM, add test coverage
4. **Low**: Configure hardcoded values, optimize CircularScrollingSelector