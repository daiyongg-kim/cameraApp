package com.daiyongk.neica

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite runner for all unit tests
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    FilmEffectTest::class,
    FilterProcessorTest::class,
    CameraSettingsManagerTest::class
)
class TestRunner