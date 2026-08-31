package com.forgetmenot

import kotlin.test.Test
import kotlin.test.assertEquals

class UiScaleTest {

    @Test
    fun theOverrideWinsOverEverything() {
        assertEquals(2f, resolveUiScale(override = "2", detected = "1.4", fallback = 1f))
    }

    @Test
    fun theDetectedScaleIsUsedWhenThereIsNoOverride() {
        assertEquals(1.4f, resolveUiScale(override = null, detected = "1.4", fallback = 1f))
    }

    @Test
    fun theFallbackIsUsedWhenNothingElseIsAvailable() {
        assertEquals(2f, resolveUiScale(override = null, detected = null, fallback = 2f))
    }

    @Test
    fun surroundingWhitespaceIsTolerated() {
        assertEquals(1.5f, resolveUiScale(override = " 1.5 ", detected = null, fallback = 1f))
    }

    @Test
    fun unparseableValuesAreIgnoredRatherThanFatal() {
        assertEquals(1.4f, resolveUiScale(override = "big", detected = "1.4", fallback = 1f))
        assertEquals(1f, resolveUiScale(override = "", detected = "", fallback = 1f))
    }

    @Test
    fun nonsensicalScalesFallThroughInsteadOfBrickingTheWindow() {
        // A typo like 14 instead of 1.4 would otherwise leave nothing on screen.
        assertEquals(1f, resolveUiScale(override = "14", detected = null, fallback = 1f))
        assertEquals(1f, resolveUiScale(override = "0", detected = null, fallback = 1f))
        assertEquals(1f, resolveUiScale(override = "-2", detected = null, fallback = 1f))
    }

    @Test
    fun aBadOverrideStillFallsBackToTheDetectedScale() {
        assertEquals(1.4f, resolveUiScale(override = "0", detected = "1.4", fallback = 1f))
    }
}
