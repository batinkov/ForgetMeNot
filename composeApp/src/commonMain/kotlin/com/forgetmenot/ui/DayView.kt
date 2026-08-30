package com.forgetmenot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.forgetmenot.domain.ReminderEvent
import com.forgetmenot.domain.on
import kotlinx.datetime.LocalDate

/**
 * The type scale and spacing for one window size class.
 *
 * Every piece of text in the day view takes its size from here — there are no
 * literal font sizes anywhere else. That is the point: when a size class has to
 * step up, there is exactly one table to change, and nothing can be left behind
 * at a smaller class's size.
 *
 * Text is in sp (which honours the reader's OS font-size preference); spacing
 * and anything geometric is in dp; letter spacing is in em so it tracks the
 * font size it is applied to.
 */
data class DayMetrics(
    // Type
    val brandSize: TextUnit,
    val weekdaySize: TextUnit,
    val dateSize: TextUnit,
    val categoryLabelSize: TextUnit,
    val nameSize: TextUnit,
    val secondarySize: TextUnit,
    val emptySize: TextUnit,
    val metaSize: TextUnit,
    val devSize: TextUnit,
    // Space
    val iconSize: Dp,
    val iconGap: Dp,
    val markSize: Dp,
    val cardPadding: PaddingValues,
    val cardGap: Dp,
    val screenPadding: Dp,
    val topPadding: Dp,
    val columnMaxWidth: Dp,
    val railWidth: Dp,
) {
    /** Touch target around the done mark: never below the 44dp floor. */
    val markTarget: Dp get() = maxOf(44.dp, markSize + 18.dp)

    companion object {
        /** Phones — Material 3 Compact, below 600dp. */
        val Compact = DayMetrics(
            brandSize = 11.sp,
            weekdaySize = 11.sp,
            dateSize = 38.sp,
            categoryLabelSize = 10.5.sp,
            nameSize = 23.sp,
            secondarySize = 13.5.sp,
            emptySize = 28.sp,
            metaSize = 12.5.sp,
            devSize = 11.sp,
            iconSize = 20.dp,
            iconGap = 13.dp,
            markSize = 26.dp,
            cardPadding = PaddingValues(horizontal = 16.dp, vertical = 15.dp),
            cardGap = 10.dp,
            screenPadding = 24.dp,
            topPadding = 48.dp,
            columnMaxWidth = 560.dp,
            railWidth = 0.dp,
        )

        /** Tablets — Material 3 Medium, 600dp to 839dp. */
        val Medium = Compact.copy(
            brandSize = 12.sp,
            weekdaySize = 12.5.sp,
            dateSize = 50.sp,
            categoryLabelSize = 11.5.sp,
            nameSize = 26.sp,
            secondarySize = 15.sp,
            emptySize = 34.sp,
            metaSize = 13.5.sp,
            devSize = 12.sp,
            iconSize = 23.dp,
            iconGap = 15.dp,
            markSize = 28.dp,
            cardPadding = PaddingValues(horizontal = 19.dp, vertical = 18.dp),
            cardGap = 12.dp,
            screenPadding = 36.dp,
            topPadding = 60.dp,
            columnMaxWidth = 640.dp,
        )

        /** Desktops — Material 3 Expanded, 840dp and up. */
        val Expanded = Compact.copy(
            brandSize = 14.sp,
            weekdaySize = 15.sp,
            dateSize = 68.sp,
            categoryLabelSize = 12.5.sp,
            nameSize = 29.sp,
            secondarySize = 16.sp,
            emptySize = 42.sp,
            metaSize = 15.sp,
            devSize = 13.sp,
            iconSize = 26.dp,
            iconGap = 18.dp,
            markSize = 32.dp,
            cardPadding = PaddingValues(horizontal = 24.dp, vertical = 22.dp),
            cardGap = 14.dp,
            screenPadding = 64.dp,
            topPadding = 72.dp,
            columnMaxWidth = 720.dp,
            railWidth = 340.dp,
        )
    }
}

/**
 * The day view: everything happening on [today], and nothing else.
 *
 * "Done" is held here rather than inside each card, and keyed on [today], so it
 * resets when the day changes — which is the correct lifetime for it, and
 * leaves one place to persist it later.
 */
@Composable
fun DayView(
    allEvents: List<ReminderEvent>,
    today: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val events = remember(allEvents, today) { allEvents.on(today) }
    var done by remember(today) { mutableStateOf(emptySet<ReminderEvent>()) }

    BoxWithConstraints(modifier.fillMaxSize().background(Paper.ground)) {
        val metrics = when {
            maxWidth < 600.dp -> DayMetrics.Compact
            maxWidth < 840.dp -> DayMetrics.Medium
            else -> DayMetrics.Expanded
        }
        // The size class sets the type scale; the masthead layout needs enough
        // width for two comfortable columns, which is more than Expanded's floor.
        val useRail = maxWidth >= 1000.dp

        val toggle: (ReminderEvent) -> Unit = { e ->
            done = if (e in done) done - e else done + e
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (useRail) {
                    RailDay(events, today, metrics, done, toggle)
                } else {
                    StackedDay(events, today, metrics, done, toggle)
                }
            }
            DevDateBar(
                today = today,
                allEvents = allEvents,
                metrics = metrics,
                onDateChange = onDateChange,
            )
        }
    }
}

/** Phone and tablet: date on top, one centred column beneath. */
@Composable
private fun StackedDay(
    events: List<ReminderEvent>,
    today: LocalDate,
    m: DayMetrics,
    done: Set<ReminderEvent>,
    onToggle: (ReminderEvent) -> Unit,
) {
    val measure = Modifier.widthIn(max = m.columnMaxWidth + m.screenPadding * 2).fillMaxWidth()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = measure
                .padding(horizontal = m.screenPadding)
                .padding(top = m.topPadding, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(today.weekdayName(), style = smallCaps(m.weekdaySize, Paper.inkLabel))
            Text(today.dayAndMonth(), style = displayDate(m.dateSize))
        }

        Box(measure.padding(horizontal = m.screenPadding)) { Rule() }

        if (events.isEmpty()) {
            EmptyDay(m, Modifier.weight(1f))
        } else {
            Column(
                modifier = measure
                    .weight(1f)
                    .padding(horizontal = m.screenPadding, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(m.cardGap),
            ) {
                events.forEach { event ->
                    EventCard(event, today, m, event in done, onToggle = { onToggle(event) })
                }
            }
        }
    }
}

/** Wide screens: the date becomes a masthead in a left rail, events to its right. */
@Composable
private fun RailDay(
    events: List<ReminderEvent>,
    today: LocalDate,
    m: DayMetrics,
    done: Set<ReminderEvent>,
    onToggle: (ReminderEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = m.screenPadding, vertical = m.topPadding),
        horizontalArrangement = Arrangement.spacedBy(64.dp),
    ) {
        Column(
            modifier = Modifier.width(m.railWidth),
            verticalArrangement = Arrangement.spacedBy(34.dp),
        ) {
            Text(
                text = "FORGETMENOT",
                style = smallCaps(m.brandSize, Paper.inkFaint, FontWeight.Bold, 0.19),
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(today.weekdayName(), style = smallCaps(m.weekdaySize, Paper.inkLabel))
                Text(today.dayOfMonth.toString(), style = displayDate(m.dateSize))
                Text(today.monthName(), style = displayDate(m.dateSize))
            }
            if (events.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(Modifier.width(48.dp).height(1.dp).background(Paper.markOutline))
                    Text(countLine(events.size), style = body(m.metaSize, Paper.inkTertiary))
                }
            }
        }

        if (events.isEmpty()) {
            EmptyDay(m, Modifier.weight(1f))
        } else {
            Column(
                modifier = Modifier.weight(1f).widthIn(max = m.columnMaxWidth),
                verticalArrangement = Arrangement.spacedBy(m.cardGap),
            ) {
                events.forEach { event ->
                    EventCard(event, today, m, event in done, onToggle = { onToggle(event) })
                }
            }
        }
    }
}

/**
 * Deliberately not a blank screen: an empty app is otherwise indistinguishable
 * from a broken one.
 */
@Composable
private fun EmptyDay(m: DayMetrics, modifier: Modifier = Modifier) {
    val ruleWidth = if (m.emptySize.value > 34f) 44.dp else 34.dp
    Column(
        modifier = modifier.fillMaxSize().padding(bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(26.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(Modifier.width(ruleWidth).height(1.dp).background(Paper.markOutline))
            Box(Modifier.size(5.dp).clip(CircleShape).background(Paper.markOutline))
            Box(Modifier.width(ruleWidth).height(1.dp).background(Paper.markOutline))
        }
        Text(
            text = "Nothing today",
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = m.emptySize,
                fontWeight = FontWeight.Light,
                color = Paper.inkSecondary,
            ),
        )
    }
}

@Composable
private fun Rule() {
    Spacer(Modifier.fillMaxWidth().height(1.dp).background(Paper.rule))
}

private fun countLine(n: Int): String = when (n) {
    0 -> "Nothing today"
    1 -> "One thing today"
    else -> "$n things today"
}

internal fun smallCaps(
    size: TextUnit,
    color: Color,
    weight: FontWeight = FontWeight.SemiBold,
    tracking: Double = 0.15,
) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontSize = size,
    fontWeight = weight,
    letterSpacing = tracking.em,
    color = color,
)

private fun displayDate(size: TextUnit) = TextStyle(
    fontFamily = FontFamily.Serif,
    fontSize = size,
    fontWeight = FontWeight.Normal,
    lineHeight = size * 1.0f,
    color = Paper.ink,
)

internal fun body(size: TextUnit, color: Color) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontSize = size,
    color = color,
)

private fun LocalDate.weekdayName(): String = dayOfWeek.name.uppercase()

private fun LocalDate.monthName(): String =
    month.name.lowercase().replaceFirstChar { it.uppercase() }

private fun LocalDate.dayAndMonth(): String = "$dayOfMonth ${monthName()}"
