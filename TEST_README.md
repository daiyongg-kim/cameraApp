# Camera App Test Suite

This document describes the comprehensive test suite for the NEICA camera app.

## Test Structure

### Unit Tests (`/app/src/test/`)
- **FilmEffectTest.kt** - Tests for FilmEffect data models and FilmEffects object
- **FilterProcessorTest.kt** - Tests for image filter processing logic
- **CameraSettingsManagerTest.kt** - Tests for settings persistence and management

### Instrumented Tests (`/app/src/androidTest/`)
- **MainActivityTest.kt** - Integration tests for main UI components
- **CameraInstrumentedTest.kt** - Tests for camera functionality and file operations
- **UIInteractionTest.kt** - Comprehensive UI interaction and user flow tests

## Running Tests

### Run All Tests
```bash
./gradlew test                    # Run all unit tests
./gradlew connectedAndroidTest    # Run all instrumented tests
./gradlew check                   # Run all tests and lint checks
```

### Run Specific Test Classes
```bash
# Unit tests
./gradlew test --tests FilmEffectTest
./gradlew test --tests FilterProcessorTest
./gradlew test --tests CameraSettingsManagerTest

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest --tests MainActivityTest
./gradlew connectedAndroidTest --tests CameraInstrumentedTest
./gradlew connectedAndroidTest --tests UIInteractionTest
```

### Run with Coverage
```bash
./gradlew testDebugUnitTestCoverage           # Unit test coverage
./gradlew createDebugCoverageReport           # Instrumented test coverage
```

### View Test Results
Test results are generated in:
- Unit tests: `app/build/reports/tests/testDebugUnitTest/index.html`
- Instrumented tests: `app/build/reports/androidTests/connected/index.html`
- Coverage reports: `app/build/reports/coverage/debug/index.html`

## Test Coverage

### Unit Test Coverage
- **FilmEffect**: 100% - All properties and methods tested
- **FilterProcessor**: ~90% - Core filter logic and matrix operations tested
- **CameraSettingsManager**: 100% - All settings and persistence tested

### Integration Test Coverage
- **UI Components**: ~80% - Main UI elements and interactions tested
- **Camera Operations**: ~70% - Photo capture and processing tested
- **Settings Dialogs**: ~90% - All settings UI interactions tested
- **Gallery Features**: ~60% - Basic gallery functionality tested

## Test Requirements

### For Unit Tests
- No special requirements, runs on JVM
- Uses Robolectric for Android framework mocking

### For Instrumented Tests
- Android device or emulator required
- Camera permission must be grantable
- Minimum SDK 24 (Android 7.0)
- Recommended: Use emulator with camera support

## Continuous Integration

Add to your CI pipeline:

```yaml
# Example GitHub Actions workflow
test:
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v2
    - uses: actions/setup-java@v2
      with:
        java-version: '17'
    - run: ./gradlew test
    - run: ./gradlew lint
```

## Test Data

### Mock Data
- Test bitmaps created programmatically
- Film effects use production data
- Settings use temporary SharedPreferences

### Test Isolation
- Each test clears photo directory before running
- Settings are reset between tests
- No network calls required

## Known Issues

1. **Toast Testing**: Toast messages can't be easily tested with current setup
2. **Camera Preview**: Actual camera preview requires physical device
3. **Permission Dialogs**: Permission grant is automatic in tests
4. **Image Capture**: Mock implementation used in unit tests

## Adding New Tests

### Unit Test Template
```kotlin
@Test
fun `test description here`() {
    // Arrange
    val testObject = TestClass()
    
    // Act
    val result = testObject.performAction()
    
    // Assert
    assertEquals(expected, result)
}
```

### UI Test Template
```kotlin
@Test
fun testUIInteraction() {
    composeTestRule.onNodeWithText("Button")
        .assertExists()
        .performClick()
    
    composeTestRule.onNodeWithText("Result")
        .assertIsDisplayed()
}
```

## Best Practices

1. **Test Naming**: Use descriptive names with backticks for readability
2. **Test Independence**: Each test should be independent and not rely on others
3. **Resource Cleanup**: Always clean up bitmaps and files after tests
4. **Assertions**: Use specific assertions rather than generic assertTrue/False
5. **Wait Strategies**: Use `waitForIdle()` and `waitUntil()` for async operations
6. **Test Tags**: Add test tags to UI components for easier testing

## Debugging Tests

### Enable Logging
```kotlin
Log.d("TEST", "Debug message")
```

### Capture Screenshots (Instrumented)
```kotlin
composeTestRule.onRoot().captureToImage()
```

### Inspect UI Tree
```kotlin
composeTestRule.onRoot().printToLog("UI_TREE")
```

## Performance Testing

For performance-critical components like FilterProcessor:

```kotlin
@Test
fun testFilterPerformance() {
    val bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888)
    
    val startTime = System.currentTimeMillis()
    bitmap.applyFilmEffect(FilmEffects.VIV, 100f)
    val duration = System.currentTimeMillis() - startTime
    
    assertTrue("Filter should apply in < 500ms", duration < 500)
}
```

## Contact

For test-related questions or issues, please refer to the main project documentation.