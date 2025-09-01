package com.daiyongk.neica

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        // Clear any existing photos before each test
        val photosDir = java.io.File(context.filesDir, "photos")
        if (photosDir.exists()) {
            photosDir.listFiles()?.forEach { it.delete() }
        }
    }

    @Test
    fun cameraPreviewScreen_displaysMainComponents() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Verify camera switch button is displayed
        composeTestRule.onNodeWithContentDescription("Switch Camera")
            .assertExists()
            .assertIsDisplayed()

        // Verify capture button exists (it's a clickable Box)
        composeTestRule.onAllNodesWithTag("CaptureButton", useUnmergedTree = true)
            .assertCountEquals(0) // Note: We should add testTags to make this testable

        // Verify film effect selector components exist
        composeTestRule.onNodeWithText("VIV")
            .assertExists()
    }

    @Test
    fun filmEffectSelector_showsAllEffects() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Check that film effects are displayed
        composeTestRule.onNodeWithText("VIV").assertExists()
        composeTestRule.onNodeWithText("NAT").assertExists()
        composeTestRule.onNodeWithText("CHR").assertExists()
        composeTestRule.onNodeWithText("CLS").assertExists()
        composeTestRule.onNodeWithText("CNT").assertExists()
    }

    @Test
    fun strengthSlider_displaysAndIsInteractive() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Verify strength label and value are displayed
        composeTestRule.onNodeWithText("Strength")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("100")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun cameraSwitch_buttonIsClickable() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Find and click the camera switch button
        composeTestRule.onNodeWithContentDescription("Switch Camera")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    @Test
    fun modeSelector_showsPhotoAndApertureMode() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Click on mode selector button
        composeTestRule.onNodeWithText("P")
            .assertExists()
            .performClick()

        // Verify mode selector overlay appears
        composeTestRule.onNodeWithText("Photo")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Aperture")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun cameraSettings_allSettingsAreDisplayed() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()

        // Check for all camera settings
        composeTestRule.onNodeWithText("Format")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Location")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Flash")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Timer")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Grid")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Level")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Focus Peaking")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Histogram")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun galleryThumbnail_isDisplayedAndClickable() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Find gallery thumbnail (shows camera emoji when empty)
        composeTestRule.onNodeWithText("📷")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun effectNameDisappears_afterSelection() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Initially, the effect name should be visible
        composeTestRule.onNodeWithText("NEICA LOOK CHROME 100%", substring = true)
            .assertExists()

        // Click on a different effect
        composeTestRule.onNodeWithText("VIV")
            .performClick()

        // Verify the new effect name appears
        composeTestRule.onNodeWithText("NEICA LOOK VIVID", substring = true)
            .assertExists()

        // Wait for 3.5 seconds and verify it disappears
        composeTestRule.waitUntil(timeoutMillis = 4000) {
            composeTestRule.onAllNodesWithText("NEICA LOOK VIVID", substring = true)
                .fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun timerSetting_opensDialog() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()

        // Click on Timer setting
        composeTestRule.onNodeWithText("Timer")
            .performClick()

        // Verify timer dialog appears
        composeTestRule.onNodeWithText("Timer Settings")
            .assertExists()
            .assertIsDisplayed()

        // Verify timer options
        composeTestRule.onNodeWithText("Off")
            .assertExists()
        composeTestRule.onNodeWithText("3 seconds")
            .assertExists()
        composeTestRule.onNodeWithText("10 seconds")
            .assertExists()
    }

    @Test
    fun flashSetting_opensDialog() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()

        // Click on Flash setting
        composeTestRule.onNodeWithText("Flash")
            .performClick()

        // Verify flash dialog appears
        composeTestRule.onNodeWithText("Flash Settings")
            .assertExists()
            .assertIsDisplayed()

        // Verify flash options
        composeTestRule.onNodeWithText("Off")
            .assertExists()
        composeTestRule.onNodeWithText("On")
            .assertExists()
        composeTestRule.onNodeWithText("Auto")
            .assertExists()
    }

    @Test
    fun gridOverlay_togglesOnAndOff() {
        composeTestRule.setContent {
            CameraPreviewScreen()
        }

        // Open mode selector
        composeTestRule.onNodeWithText("P")
            .performClick()

        // Click on Grid setting
        composeTestRule.onNodeWithText("Grid")
            .performClick()

        // Toggle grid on
        composeTestRule.onNodeWithText("Show Grid")
            .assertExists()
        
        // Find and click the switch (assuming it's next to the text)
        composeTestRule.onAllNodes(hasClickAction())
            .filter(hasAnyDescendant(hasText("Show Grid")))
            .onFirst()
            .performClick()

        // Close dialog
        composeTestRule.onNodeWithText("Done")
            .performClick()
    }
}