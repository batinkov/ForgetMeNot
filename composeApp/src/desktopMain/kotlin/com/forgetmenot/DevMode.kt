package com.forgetmenot

/**
 * Whether to show the developer date bar.
 *
 * Off unless asked for, so a packaged build cannot ship the strip by accident.
 * This is a runtime flag rather than a compile-time one because Kotlin
 * Multiplatform has no BuildConfig: the alternatives are a Gradle plugin for a
 * single boolean, or this. The dev code therefore ships in the binary and is
 * merely not drawn — which is fine for a date stepper, and would not be for
 * anything worth hiding.
 */
internal fun devToolsEnabled(raw: String?): Boolean =
    raw?.trim()?.lowercase() in setOf("1", "true", "yes", "on")
