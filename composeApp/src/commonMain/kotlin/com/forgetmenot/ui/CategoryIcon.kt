package com.forgetmenot.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.forgetmenot.domain.EventCategory

/**
 * One stroke icon per category, drawn on a 20x20 grid so they share a weight
 * and scale cleanly. Hand-drawn rather than pulled from an icon set because
 * none of them ship an Orthodox cross or a nameday sprig.
 */
@Composable
fun CategoryIcon(
    category: EventCategory,
    tint: Color,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.size(size)) {
        val u = this.size.minDimension / 20f
        val stroke = Stroke(width = 1.4f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)

        fun line(x1: Float, y1: Float, x2: Float, y2: Float) = drawLine(
            color = tint,
            start = Offset(x1 * u, y1 * u),
            end = Offset(x2 * u, y2 * u),
            strokeWidth = 1.4f * u,
            cap = StrokeCap.Round,
        )

        fun shape(build: Pen.() -> Unit) {
            val pen = Pen(u)
            pen.build()
            drawPath(pen.path, color = tint, style = stroke)
        }

        when (category) {
            EventCategory.BIRTHDAY -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(3.2f * u, 10.4f * u),
                    size = Size(13.6f * u, 6.2f * u),
                    cornerRadius = CornerRadius(1.3f * u),
                    style = stroke,
                )
                line(10f, 10.4f, 10f, 7.4f)
                line(3.2f, 13.4f, 16.8f, 13.4f)
                shape {
                    move(10f, 3.6f)
                    quad(11.5f, 5.1f, 10f, 6.2f)
                    quad(8.5f, 5.1f, 10f, 3.6f)
                }
            }

            EventCategory.NAMEDAY -> {
                line(10f, 17.2f, 10f, 7f)
                shape {
                    move(10f, 11.2f)
                    quad(10.6f, 7.4f, 14.2f, 7f)
                    quad(13.8f, 10.6f, 10f, 11.2f)
                }
                shape {
                    move(10f, 14.6f)
                    quad(9.4f, 10.8f, 5.8f, 10.4f)
                    quad(6.2f, 14f, 10f, 14.6f)
                }
            }

            EventCategory.ANNIVERSARY -> {
                drawCircle(tint, radius = 4.4f * u, center = Offset(7.7f * u, 10f * u), style = stroke)
                drawCircle(tint, radius = 4.4f * u, center = Offset(12.3f * u, 10f * u), style = stroke)
            }

            EventCategory.MEMORIAL -> {
                shape {
                    move(10f, 3.4f)
                    quad(12.5f, 6.0f, 12.5f, 7.9f)
                    quad(12.5f, 10.4f, 10f, 10.4f)
                    quad(7.5f, 10.4f, 7.5f, 7.9f)
                    quad(7.5f, 6.0f, 10f, 3.4f)
                }
                line(10f, 10.4f, 10f, 16.6f)
                line(7.3f, 16.6f, 12.7f, 16.6f)
            }

            EventCategory.FEAST -> {
                line(10f, 2.9f, 10f, 17.3f)
                line(7.3f, 5.9f, 12.7f, 5.9f)
                line(5.6f, 9.1f, 14.4f, 9.1f)
                line(7.4f, 13.6f, 12.6f, 11.9f)
            }

            EventCategory.NATIONAL_DAY -> {
                line(5.6f, 3.1f, 5.6f, 17.3f)
                shape {
                    move(5.6f, 4.5f)
                    line(14.7f, 4.5f)
                    line(12.7f, 7.4f)
                    line(14.7f, 10.3f)
                    line(5.6f, 10.3f)
                    close()
                }
            }

            EventCategory.CUSTOM -> shape {
                move(6.1f, 3.4f)
                line(13.9f, 3.4f)
                line(13.9f, 16.6f)
                line(10f, 13.1f)
                line(6.1f, 16.6f)
                close()
            }
        }
    }
}

/**
 * Builds a [Path] from coordinates on the 20x20 icon grid, scaling by [u] as it
 * goes and remembering the current point — which [Path] itself doesn't expose,
 * and which a quadratic needs in order to be re-expressed as a cubic.
 *
 * We convert rather than call Path.quadraticBezierTo because that name is
 * deprecated in newer Compose and its replacement is absent in older ones;
 * cubicTo has been stable throughout.
 */
private class Pen(private val u: Float) {
    val path = Path()
    private var x = 0f
    private var y = 0f

    fun move(nx: Float, ny: Float) {
        path.moveTo(nx * u, ny * u)
        x = nx; y = ny
    }

    fun line(nx: Float, ny: Float) {
        path.lineTo(nx * u, ny * u)
        x = nx; y = ny
    }

    /** Quadratic through control point ([qx], [qy]) to ([nx], [ny]). */
    fun quad(qx: Float, qy: Float, nx: Float, ny: Float) {
        val third = 2f / 3f
        path.cubicTo(
            (x + third * (qx - x)) * u, (y + third * (qy - y)) * u,
            (nx + third * (qx - nx)) * u, (ny + third * (qy - ny)) * u,
            nx * u, ny * u,
        )
        x = nx; y = ny
    }

    fun close() = path.close()
}
