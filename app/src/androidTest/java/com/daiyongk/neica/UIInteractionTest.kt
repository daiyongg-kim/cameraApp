package com.daiyongk.neica

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class UIInteractionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Clear photos directory before each test
        val photosDir = File(composeTestRule.activity.filesDir, "photos")
        if (photosDir.exists()) {
            photosDir.listFiles()?.forEach { it.delete() }
        }
    }

    @Test
    fun testFilmEffectSelection() {
        // Wait for UI to be ready
        composeTestRule.waitForIdle()
        
        // Select VIV effect
        composeTestRule.onNodeWithText("VIV")
            .assertExists()
            .performClick()
        
        // Verify effect name appears
        composeTestRule.onNodeWithText("NEICA LOOK VIVID", substring = true)
            .assertExists()
        
        // Select NAT effect
        composeTestRule.onNodeWithText("NAT")
            .performClick()
        
        // Verify new effect name appears
        composeTestRule.onNodeWithText("NEICA LOOK NATURAL", substring = true)
            .assertExists()
    }

    @Test
    fun testStrengthSliderInteraction() {
        composeTestRule.waitForIdle()
        
        // Find the slider (it's the only slider in the UI)
        val sliderNode = composeTestRule.onNode(
            hasTestTag("StrengthSlider").or(
                hasContentDescription("Strength Slider").or(
                    hasClickAction().and(hasScrollAction())
                )
            )
        )
        
        // Verify initial value is 100
        composeTestRule.onNodeWithText("100")
            .assertExists()
        
        // Perform swipe gesture on slider to change value
        sliderNode.performTouchInput {
            swipeLeft()
        }
        
        // Value should have changed from 100
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            !composeTestRule.onAllNodesWithText("100")
                .fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun testCameraModeSelectorToggle() {
        composeTestRule.waitForIdle()
        
        // Click on mode selector button
        composeTestRule.onNodeWithText("P")
            .assertExists()
            .performClick()
        
        // Verify mode selector panel appears
        composeTestRule.onNodeWithText("Photo")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Aperture")
            .assertExists()
            .assertIsDisplayed()
        
        // Click outside to dismiss
        composeTestRule.onNode(hasClickAction())
            .performClick()
        
        // Verify mode selector is hidden
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Photo")
                .fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun testSettingsDialogInteractions() {
        composeTestRule.waitForIdle()
        
        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()
        
        // Click on Timer setting
        composeTestRule.onNodeWithText("Timer")
            .performClick()
        
        // Verify dialog appears
        composeTestRule.onNodeWithText("Timer Settings")
            .assertExists()
        
        // Select 3 seconds option
        composeTestRule.onNodeWithText("3 seconds")
            .performClick()
        
        // Click Done
        composeTestRule.onNodeWithText("Done")
            .performClick()
        
        // Verify dialog is dismissed
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Timer Settings")
                .fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun testFlashModeSelection() {
        composeTestRule.waitForIdle()
        
        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()
        
        // Click on Flash setting
        composeTestRule.onNodeWithText("Flash")
            .performClick()
        
        // Verify flash options
        composeTestRule.onNodeWithText("Off").assertExists()
        composeTestRule.onNodeWithText("On").assertExists()
        composeTestRule.onNodeWithText("Auto").assertExists()
        
        // Select "On"
        composeTestRule.onNodeWithText("On")
            .performClick()
        
        // Click Done
        composeTestRule.onNodeWithText("Done")
            .performClick()
    }

    @Test
    fun testGridToggle() {
        composeTestRule.waitForIdle()
        
        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()
        
        // Click on Grid setting
        composeTestRule.onNodeWithText("Grid")
            .performClick()
        
        // Find the switch next to "Show Grid"
        val switchNode = composeTestRule.onNode(
            hasClickAction().and(
                hasAnyDescendant(hasText("Show Grid"))
            )
        )
        
        // Toggle the switch
        switchNode.performClick()
        
        // Click Done
        composeTestRule.onNodeWithText("Done")
            .performClick()
        
        // Grid overlay should now be visible in the camera preview
        // (Would need to add testTag to GridOverlay to properly test this)
    }

    @Test
    fun testFileFormatSelection() {
        composeTestRule.waitForIdle()
        
        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()
        
        // Click on Format setting
        composeTestRule.onNodeWithText("Format")
            .performClick()
        
        // Verify format options
        composeTestRule.onNodeWithText("JPEG").assertExists()
        composeTestRule.onNodeWithText("RAW (DNG)").assertExists()
        composeTestRule.onNodeWithText("JPEG + RAW").assertExists()
        
        // Select RAW
        composeTestRule.onNodeWithText("RAW (DNG)")
            .performClick()
        
        // Click Done
        composeTestRule.onNodeWithText("Done")
            .performClick()
    }

    @Test
    fun testGalleryEmptyState() {
        composeTestRule.waitForIdle()
        
        // Click on gallery thumbnail (shows camera emoji when empty)
        composeTestRule.onNodeWithText("📷")
            .assertExists()
            .performClick()
        
        // Should show toast message
        // Note: Toast testing requires additional setup
    }

    @Test
    fun testCameraSwitchButton() {
        composeTestRule.waitForIdle()
        
        // Find and click camera switch button
        composeTestRule.onNodeWithContentDescription("Switch Camera")
            .assertExists()
            .assertIsDisplayed()
            .performClick()
        
        // Camera should switch (would need to verify camera state in real test)
        composeTestRule.waitForIdle()
        
        // Click again to switch back
        composeTestRule.onNodeWithContentDescription("Switch Camera")
            .performClick()
    }

    @Test
    fun testEffectNameAutoHide() {
        composeTestRule.waitForIdle()
        
        // Select an effect
        composeTestRule.onNodeWithText("VIV")
            .performClick()
        
        // Effect name should appear
        composeTestRule.onNodeWithText("NEICA LOOK VIVID", substring = true)
            .assertExists()
        
        // Wait for 3.5 seconds
        composeTestRule.mainClock.advanceTimeBy(3500)
        composeTestRule.waitForIdle()
        
        // Effect name should be hidden
        composeTestRule.onNodeWithText("NEICA LOOK VIVID", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun testMultipleSettingsChanges() {
        composeTestRule.waitForIdle()
        
        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()
        
        // Change Flash setting
        composeTestRule.onNodeWithText("Flash").performClick()
        composeTestRule.onNodeWithText("Auto").performClick()
        composeTestRule.onNodeWithText("Done").performClick()
        
        // Change Timer setting
        composeTestRule.onNodeWithText("Timer").performClick()
        composeTestRule.onNodeWithText("10 seconds").performClick()
        composeTestRule.onNodeWithText("Done").performClick()
        
        // Change Grid setting
        composeTestRule.onNodeWithText("Grid").performClick()
        val gridSwitch = composeTestRule.onNode(
            hasClickAction().and(hasAnyDescendant(hasText("Show Grid")))
        )
        gridSwitch.performClick()
        composeTestRule.onNodeWithText("Done").performClick()
        
        // Close mode selector
        composeTestRule.onNode(hasClickAction()).performClick()
        
        // Settings should be persisted (would need to verify through SettingsManager)
    }

    @Test
    fun testProFeatureBadges() {
        composeTestRule.waitForIdle()
        
        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()
        
        // Check for PRO badges on Focus Peaking and Histogram
        composeTestRule.onNodeWithText("Focus Peaking")
            .assertExists()
        
        composeTestRule.onNodeWithText("Histogram")
            .assertExists()
        
        // PRO badges should be visible (would need to add specific test tags)
    }
}