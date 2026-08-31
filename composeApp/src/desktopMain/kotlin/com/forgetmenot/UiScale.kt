package com.forgetmenot

/** Scales outside this range are almost certainly a typo, and would leave the app unusable. */
private val SANE_RANGE = 0.5f..4f

/**
 * How much to scale the UI by.
 *
 * Compose Desktop takes its density from the AWT graphics transform, which under
 * XWayland is the identity matrix no matter what the compositor is doing — so on
 * a fractionally-scaled Linux desktop the app renders at 1.0 while every other
 * window is scaled, and looks about 30% too small. Java itself detects the scale
 * correctly and puts it in `sun.java2d.uiScale`, so we prefer that.
 *
 * @param override an explicit value (FORGETMENOT_UI_SCALE), for when the
 *   detected one is wrong or absent. Wins over everything.
 * @param detected what the JVM worked out for itself.
 * @param fallback what Compose would have used anyway — already correct on
 *   macOS, Windows, and Linux desktops that scale by whole numbers.
 */
internal fun resolveUiScale(
    override: String?,
    detected: String?,
    fallback: Float,
): Float =
    parseScale(override) ?: parseScale(detected) ?: fallback

private fun parseScale(raw: String?): Float? {
    val value = raw?.trim()?.toFloatOrNull() ?: return null
    return value.takeIf { it.isFinite() && it in SANE_RANGE }
}
