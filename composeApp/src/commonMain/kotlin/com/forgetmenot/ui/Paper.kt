package com.forgetmenot.ui

import androidx.compose.ui.graphics.Color
import com.forgetmenot.domain.EventCategory

/**
 * The "quiet paper" palette. Neutrals are warm — nothing here is a pure grey —
 * and every accent sits at one lightness and chroma in oklch, with only the hue
 * moving, so no category can shout over another.
 *
 * Memorial is the deliberate exception: its chroma is dropped to almost nothing
 * so it reads as a hush beside a birthday rather than as a seventh colour.
 */
object Paper {
    val ground = Color(0xFFFBF8F2)
    val card = Color(0xFFFFFDF9)
    val cardQuiet = Color(0xFFFDFAF5)

    val rule = Color(0xFFEAE2D5)
    val ruleQuiet = Color(0xFFEDE6DB)
    val markOutline = Color(0xFFDCD3C5)

    val ink = Color(0xFF211D19)
    val inkQuiet = Color(0xFF3A342E)
    val inkSecondary = Color(0xFF6A6158)
    val inkTertiary = Color(0xFF8A8078)
    val inkLabel = Color(0xFF9A9085)
    val inkFaint = Color(0xFFB3A99C)
}

/** oklch(0.5x 0.0x H) resolved to sRGB — see the accent strip in the design canvas. */
fun accentFor(category: EventCategory): Color = when (category) {
    EventCategory.BIRTHDAY -> Color(0xFF98602A)
    EventCategory.NAMEDAY -> Color(0xFF3C7145)
    EventCategory.ANNIVERSARY -> Color(0xFFA24F4E)
    EventCategory.MEMORIAL -> Color(0xFF656875)
    EventCategory.FEAST -> Color(0xFF795284)
    EventCategory.NATIONAL_DAY -> Color(0xFF3B6695)
    EventCategory.CUSTOM -> Color(0xFF147172)
}

/** Memorial cards sit on a slightly softer ground and use a lighter ink. */
fun cardSurfaceFor(category: EventCategory): Color =
    if (category == EventCategory.MEMORIAL) Paper.cardQuiet else Paper.card

fun cardBorderFor(category: EventCategory): Color =
    if (category == EventCategory.MEMORIAL) Paper.ruleQuiet else Paper.rule

fun nameInkFor(category: EventCategory): Color =
    if (category == EventCategory.MEMORIAL) Paper.inkQuiet else Paper.ink

fun secondaryInkFor(category: EventCategory): Color =
    if (category == EventCategory.MEMORIAL) Paper.inkTertiary else Paper.inkSecondary
