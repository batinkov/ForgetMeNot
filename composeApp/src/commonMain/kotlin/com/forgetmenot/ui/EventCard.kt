package com.forgetmenot.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.forgetmenot.domain.EventCategory
import com.forgetmenot.domain.ReminderEvent
import com.forgetmenot.domain.label
import com.forgetmenot.domain.secondaryLine
import kotlinx.datetime.LocalDate

/**
 * One event. The anatomy never changes between categories — icon, label, name,
 * one secondary line — so a column of mixed cards keeps a single rhythm. Only
 * the accent hue and the wording of the secondary line vary.
 */
@Composable
fun EventCard(
    event: ReminderEvent,
    today: LocalDate,
    metrics: DayMetrics,
    isDone: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = accentFor(event.category)
    val alpha by animateFloatAsState(if (isDone) 0.4f else 1f, tween(240), label = "cardFade")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clip(RoundedCornerShape(5.dp))
            .background(cardSurfaceFor(event.category))
            .border(1.dp, cardBorderFor(event.category), RoundedCornerShape(5.dp))
            .padding(metrics.cardPadding),
        horizontalArrangement = Arrangement.spacedBy(metrics.iconGap),
        verticalAlignment = Alignment.Top,
    ) {
        CategoryIcon(
            category = event.category,
            tint = accent,
            size = metrics.iconSize,
            modifier = Modifier.padding(top = 2.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = event.category.label().uppercase(),
                style = smallCaps(metrics.categoryLabelSize, accent, FontWeight.SemiBold, 0.14),
            )
            Text(
                text = event.name,
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontSize = metrics.nameSize,
                    fontWeight = if (event.category == EventCategory.MEMORIAL) {
                        FontWeight.Light
                    } else {
                        FontWeight.Normal
                    },
                    lineHeight = metrics.nameSize * 1.15f,
                    color = nameInkFor(event.category),
                ),
            )
            event.secondaryLine(today)?.let { line ->
                Text(
                    text = line,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = metrics.secondarySize,
                        lineHeight = metrics.secondarySize * 1.5f,
                        color = secondaryInkFor(event.category),
                    ),
                )
            }
        }

        DoneMark(
            accent = accent,
            isDone = isDone,
            markSize = metrics.markSize,
            target = metrics.markTarget,
            onToggle = onToggle,
        )
    }
}

/**
 * The done control. The mark grows with the size class, but its touch target
 * never drops below 44dp whatever the mark is doing.
 */
@Composable
private fun DoneMark(
    accent: Color,
    isDone: Boolean,
    markSize: Dp,
    target: Dp,
    onToggle: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(target)
            .clip(CircleShape)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(markSize)
                .clip(CircleShape)
                .background(if (isDone) accent else Color.Transparent)
                .border(1.dp, if (isDone) accent else Paper.markOutline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isDone) {
                Canvas(Modifier.size(markSize / 2)) {
                    val u = size.minDimension / 13f
                    val w = 2f * u
                    drawLine(
                        color = Paper.card,
                        start = Offset(2.7f * u, 6.9f * u),
                        end = Offset(5.3f * u, 9.5f * u),
                        strokeWidth = w,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = Paper.card,
                        start = Offset(5.3f * u, 9.5f * u),
                        end = Offset(10.4f * u, 3.5f * u),
                        strokeWidth = w,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}
