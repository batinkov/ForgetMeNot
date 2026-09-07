package com.forgetmenot

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A forget-me-not: five petals and a yellow eye. The window icon — title bar,
 * taskbar, alt-tab.
 *
 * Drawn rather than shipped as a file so it stays sharp at whatever size is
 * asked for, and so there is no binary asset to keep in step with the palette.
 * The blue is mid-toned deliberately: it sits on window decorations that may be
 * light or dark, and an icon that reads on only one of them is worse than a
 * plain shape.
 */
object ForgetMeNotIcon : Painter() {

    override val intrinsicSize: Size = Size(64f, 64f)

    override fun DrawScope.onDraw() {
        val unit = size.minDimension / 64f
        val middle = Offset(size.width / 2f, size.height / 2f)

        repeat(PETALS) { i ->
            val angle = (-90.0 + i * (360.0 / PETALS)) * PI / 180.0
            drawCircle(
                color = PETAL,
                radius = 13f * unit,
                center = Offset(
                    x = middle.x + (cos(angle) * 17.0 * unit).toFloat(),
                    y = middle.y + (sin(angle) * 17.0 * unit).toFloat(),
                ),
            )
        }
        drawCircle(color = EYE, radius = 7.5f * unit, center = middle)
    }

    private const val PETALS = 5
    private val PETAL = Color(0xFF5B7FB5)
    private val EYE = Color(0xFFE8C55A)
}
