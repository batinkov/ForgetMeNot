package com.forgetmenot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.datetime.LocalDate

/**
 * Scaffolding, not design. Most days have nothing on them, so without a way to
 * move the date you cannot see the states you are building. Deliberately styled
 * as a tool strip so nobody mistakes it for part of the app — but it still
 * takes its type size from the size class, because unreadable is unusable.
 */
@Composable
fun DevDateBar(
    date: LocalDate,
    metrics: DayMetrics,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onNextEventDay: () -> Unit,
    onPreviousEventDay: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mono = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = metrics.devSize,
        color = Paper.inkSecondary,
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Paper.rule)
            .padding(horizontal = metrics.screenPadding / 2, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "DEV",
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = metrics.devSize * 0.85f,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.12.em,
                color = Paper.inkTertiary,
            ),
        )
        Step("« prev event", mono, metrics, onPreviousEventDay)
        Step("‹", mono, metrics, onPreviousDay)
        Text(date.toString(), modifier = Modifier.padding(horizontal = 4.dp), style = mono)
        Step("›", mono, metrics, onNextDay)
        Step("next event »", mono, metrics, onNextEventDay)
        // Everything left of here moves relative to where you are; "today" is
        // absolute. The rule says they are different kinds of thing without
        // needing a different shape or colour.
        Box(
            Modifier
                .padding(horizontal = 4.dp)
                .width(1.dp)
                .height(metrics.devSize.value.dp + 4.dp)
                .background(Paper.inkFaint),
        )
        Step("today", mono, metrics, onToday)
    }
}

@Composable
private fun Step(
    label: String,
    style: TextStyle,
    metrics: DayMetrics,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(metrics.devSize.value.dp + 16.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Paper.ground)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = style)
    }
}
