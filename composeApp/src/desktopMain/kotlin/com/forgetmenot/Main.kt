package com.forgetmenot

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.forgetmenot.ui.App
import java.awt.GraphicsEnvironment

private const val WINDOW_WIDTH_DP = 1120f
private const val WINDOW_HEIGHT_DP = 820f

/** Set FORGETMENOT_UI_SCALE to override the detected scale, e.g. 1.4 or 2. */
private const val SCALE_OVERRIDE_ENV = "FORGETMENOT_UI_SCALE"

/** Set FORGETMENOT_DEV=1 to show the date bar. */
private const val DEV_TOOLS_ENV = "FORGETMENOT_DEV"

fun main() = application {
    val platformScale = platformScale()
    val uiScale = resolveUiScale(
        override = System.getenv(SCALE_OVERRIDE_ENV),
        detected = System.getProperty("sun.java2d.uiScale"),
        fallback = platformScale,
    )

    // The window is sized in the platform's own density, so it has to grow by
    // however much we are overriding by. Otherwise raising the scale shrinks the
    // content area in dp and quietly drops the layout down a size class.
    val ratio = uiScale / platformScale
    val state = rememberWindowState(
        width = (WINDOW_WIDTH_DP * ratio).dp,
        height = (WINDOW_HEIGHT_DP * ratio).dp,
    )

    val devTools = devToolsEnabled(System.getenv(DEV_TOOLS_ENV))
    if (devTools) {
        println("ForgetMeNot: developer date bar on ($DEV_TOOLS_ENV)")
    }
    if (uiScale != platformScale) {
        println("ForgetMeNot: UI scale $uiScale (platform reported $platformScale) — set $SCALE_OVERRIDE_ENV to change")
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = "ForgetMeNot",
        icon = ForgetMeNotIcon,
    ) {
        val platform = LocalDensity.current
        CompositionLocalProvider(
            LocalDensity provides Density(uiScale, platform.fontScale),
        ) {
            App(showDevTools = devTools)
        }
    }
}

/** What Compose would use unaided: correct on macOS and Windows, identity under XWayland. */
private fun platformScale(): Float = runCatching {
    GraphicsEnvironment.getLocalGraphicsEnvironment()
        .defaultScreenDevice.defaultConfiguration.defaultTransform.scaleX.toFloat()
}.getOrNull()?.takeIf { it.isFinite() && it > 0f } ?: 1f
