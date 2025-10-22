# Native Filters Implementation Checklist

Quick reference checklist for implementing native C++ film filters in your Android app.

## Prerequisites

- [ ] Android Studio installed
- [ ] Project opens successfully
- [ ] Current Kotlin filters working

## Phase 1: Setup (30 minutes)

### NDK Installation
- [ ] Open Android Studio → Tools → SDK Manager
- [ ] SDK Tools tab → Check "NDK (Side by side)"
- [ ] SDK Tools tab → Check "CMake"
- [ ] Click Apply and wait for installation
- [ ] Verify: Check `~/Android/Sdk/ndk/` exists

### Directory Structure
- [ ] Create `app/src/main/cpp/` directory
- [ ] Verify structure matches guide

## Phase 2: Configuration (20 minutes)

### Build Configuration
- [ ] Update `app/build.gradle.kts` - add NDK block to defaultConfig
- [ ] Update `app/build.gradle.kts` - add externalNativeBuild block
- [ ] Create `app/src/main/cpp/CMakeLists.txt`
- [ ] Sync Gradle: `./gradlew --refresh-dependencies`

**Validation**: Run `./gradlew assembleDebug` - should succeed

## Phase 3: C++ Implementation (1-2 hours)

### Core Files (in order)
- [ ] Create `ColorMatrix.h` - copy from guide
- [ ] Create `ColorMatrix.cpp` - copy from guide
- [ ] Create `FilterProcessor.h` - copy from guide
- [ ] Create `FilterProcessor.cpp` - copy from guide
- [ ] Create `native-lib.cpp` - copy from guide (JNI bridge)

**Validation**: Run `./gradlew assembleDebug` - check for `.so` files

### Verify Native Library
```bash
find app/build -name "*.so"
```
Expected: `libneica-filters.so` for 4 architectures

## Phase 4: Kotlin Integration (30 minutes)

### Wrapper Code
- [ ] Create `NativeFilterProcessor.kt` - copy from guide
- [ ] Update `FilterProcessor.kt` extension function
- [ ] Verify imports are correct

**Validation**: Build succeeds without errors

## Phase 5: Testing (30 minutes)

### Build and Deploy
- [ ] Clean build: `./gradlew clean`
- [ ] Build debug APK: `./gradlew assembleDebug`
- [ ] Install on device: `./gradlew installDebug`

### Runtime Testing
- [ ] Open app on device
- [ ] Take a photo
- [ ] Apply VIVID filter - should be noticeably faster
- [ ] Monitor logcat:
```bash
adb logcat | grep -E "(NativeFilterProcessor|NativeLib)"
```
- [ ] Test all 5 film effects
- [ ] Verify no crashes

### Performance Verification
- [ ] Time filter application (before: ~450ms for 1080p)
- [ ] Expected: ~80ms or faster (5-6x improvement)
- [ ] Check memory usage (should be lower)

## Phase 6: Optimization (Optional, 1-2 hours)

### Advanced Features
- [ ] Add ARM NEON SIMD optimization
- [ ] Implement multi-threading
- [ ] Profile with Android Studio Profiler
- [ ] Benchmark improvements

## Troubleshooting Common Issues

### ❌ "CMake not found"
**Fix**: Install via SDK Manager or:
```bash
$ANDROID_SDK_ROOT/tools/bin/sdkmanager "cmake;3.22.1"
```

### ❌ "couldn't find libneica-filters.so"
**Fix**:
1. Check library name in `System.loadLibrary("neica-filters")` - no "lib" prefix
2. Verify `.so` in APK: `unzip -l app/build/outputs/apk/debug/app-debug.apk | grep .so`

### ❌ Build fails with C++ errors
**Fix**:
1. Check C++17 is set in CMakeLists.txt
2. Verify all files copied correctly
3. Check file names match exactly (case-sensitive)

### ❌ JNI method not found
**Fix**:
1. Verify JNI signature in `native-lib.cpp` matches Kotlin package
2. Use `javap -s` to check generated signature
3. Ensure method name matches: `Java_com_daiyongk_neica_NativeFilterProcessor_applyFilterNative`

### ❌ App crashes with SIGSEGV
**Fix**:
1. Check bitmap is not null before processing
2. Enable AddressSanitizer in CMakeLists.txt (see guide)
3. Add null checks in C++ code

## Success Criteria

✅ App builds without errors
✅ `.so` files generated for 4 architectures
✅ Native library loads on app startup
✅ All 5 film effects work correctly
✅ 5-6x performance improvement measured
✅ No memory leaks or crashes
✅ Logcat shows "Native library loaded successfully"

## Estimated Time

- **Minimal Implementation**: 2-3 hours
- **With Testing**: 3-4 hours
- **With Optimization**: 5-6 hours

## Next Steps After Completion

1. Remove old Kotlin `FilterProcessor.applyFilter()` method
2. Update `CLAUDE.md` to reflect native implementation
3. Add performance benchmarks to README
4. Consider adding unit tests (Google Test framework)
5. Profile with larger images (4K+)

## Rollback Plan

If native implementation has issues:

```kotlin
// Keep this fallback in NativeFilterProcessor.kt
fun applyFilter(bitmap: Bitmap, filmEffect: FilmEffect, strength: Float): Boolean {
    return try {
        val result = applyFilterNative(bitmap, effectType, strength)
        result == 0
    } catch (e: Exception) {
        // Fallback to Kotlin implementation
        FilterProcessor.applyFilter(bitmap, filmEffect, strength)
        true
    }
}
```

---

**Good luck with the implementation! 🚀**

If you encounter any issues not covered in the troubleshooting section, check:
1. Android NDK documentation
2. CMake error messages carefully
3. Logcat for detailed error messages
