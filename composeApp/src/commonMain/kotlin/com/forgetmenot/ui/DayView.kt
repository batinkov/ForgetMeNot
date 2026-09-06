package com.forgetmenot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Brush
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
import com.forgetmenot.domain.UpcomingEvent
import com.forgetmenot.domain.countdownLabel
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
    val upcomingIconSize: Dp,
    val upcomingMaxHeight: Dp,
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
            upcomingIconSize = 15.dp,
            upcomingMaxHeight = 170.dp,
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
            upcomingIconSize = 16.dp,
            upcomingMaxHeight = 200.dp,
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
            upcomingIconSize = 18.dp,
            upcomingMaxHeight = 280.dp,
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
 * The day view: everything happening on the shown date, and nothing else.
 *
 * A pure function of [state] — it holds no state of its own, so every screen it
 * can show (loading, failed, empty, a full day, all done) can be produced by
 * handing it a value.
 */
@Composable
fun DayView(
    state: DayUiState,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onNextEventDay: () -> Unit,
    onPreviousEventDay: () -> Unit,
    onToday: () -> Unit,
    onToggleDone: (ReminderEvent) -> Unit,
    showDevTools: Boolean = false,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxSize().background(Paper.ground)) {
        val metrics = when {
            maxWidth < 600.dp -> DayMetrics.Compact
            maxWidth < 840.dp -> DayMetrics.Medium
            else -> DayMetrics.Expanded
        }
        // The size class sets the type scale; the masthead layout needs enough
        // width for two comfortable columns, which is more than Expanded's floor.
        val useRail = maxWidth >= 1000.dp

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (useRail) {
                    RailDay(state, metrics, onToggleDone)
                } else {
                    StackedDay(state, metrics, onToggleDone)
                }
            }
            if (showDevTools) {
                DevDateBar(
                    date = state.date,
                    metrics = metrics,
                    onPreviousDay = onPreviousDay,
                    onNextDay = onNextDay,
                    onNextEventDay = onNextEventDay,
                    onPreviousEventDay = onPreviousEventDay,
                    onToday = onToday,
                )
            }
        }
    }
}

/** Phone and tablet: date on top, one centred column beneath. */
@Composable
private fun StackedDay(
    state: DayUiState,
    m: DayMetrics,
    onToggleDone: (ReminderEvent) -> Unit,
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
            Text(state.date.weekdayName(), style = smallCaps(m.weekdaySize, Paper.inkLabel))
            Text(state.date.dayAndMonth(), style = displayDate(m.dateSize))
        }

        Box(measure.padding(horizontal = m.screenPadding)) { Rule() }

        DayBody(
            state = state,
            m = m,
            onToggleDone = onToggleDone,
            modifier = measure
                .weight(1f)
                .padding(horizontal = m.screenPadding, vertical = 22.dp),
        )

        ComingUp(
            upcoming = state.upcoming,
            m = m,
            modifier = measure.padding(horizontal = m.screenPadding, vertical = 20.dp),
        )
    }
}

/** Wide screens: the date becomes a masthead in a left rail, events to its right. */
@Composable
private fun RailDay(
    state: DayUiState,
    m: DayMetrics,
    onToggleDone: (ReminderEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = m.screenPadding, vertical = m.topPadding),
        horizontalArrangement = Arrangement.spacedBy(64.dp),
    ) {
        Column(
            modifier = Modifier.width(m.railWidth).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(34.dp),
        ) {
            Text(
                text = "FORGETMENOT",
                style = smallCaps(m.brandSize, Paper.inkFaint, FontWeight.Bold, 0.19),
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(state.date.weekdayName(), style = smallCaps(m.weekdaySize, Paper.inkLabel))
                Text(state.date.dayOfMonth.toString(), style = displayDate(m.dateSize))
                Text(state.date.monthName(), style = displayDate(m.dateSize))
            }
            Spacer(Modifier.weight(1f))
            ComingUp(upcoming = state.upcoming, m = m)
        }

        DayBody(state, m, onToggleDone, Modifier.weight(1f))
    }
}

/**
 * The four things a day can be. Loading renders nothing at all rather than
 * "Nothing today": the read is near-instant, and flashing the empty state on
 * the way to a full day would be worse than a blank moment.
 */
@Composable
private fun DayBody(
    state: DayUiState,
    m: DayMetrics,
    onToggleDone: (ReminderEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val load = state.load) {
        is LoadState.Loading -> Box(modifier)
        is LoadState.Failed -> LoadFailed(load.message, m, modifier)
        is LoadState.Ready -> if (state.events.isEmpty()) {
            EmptyDay(m, modifier)
        } else {
            // A day holds a handful of events, so a plain Column is enough — the
            // need is scrolling, not virtualisation, and this keeps the list free
            // of the unique-key requirement a LazyColumn would impose.
            FadingColumn(
                modifier = modifier.widthIn(max = m.columnMaxWidth),
                verticalArrangement = Arrangement.spacedBy(m.cardGap),
            ) {
                state.events.forEach { event ->
                    EventCard(
                        event = event,
                        today = state.date,
                        metrics = m,
                        isDone = event in state.done,
                        onToggle = { onToggleDone(event) },
                    )
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

/**
 * Said plainly, and without the empty state's ornament — this is not a calm
 * day with nothing on it, it is the app admitting it could not look.
 */
@Composable
private fun LoadFailed(message: String, m: DayMetrics, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Couldn't read the calendar",
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = m.emptySize,
                fontWeight = FontWeight.Light,
                color = Paper.inkSecondary,
            ),
        )
        Text(text = message, style = body(m.metaSize, Paper.inkTertiary))
    }
}

/**
 * Lead time, deliberately not cards. With nothing to act on and nothing to
 * emphasise, a card reduces to a row — so this is a list, not a second card
 * type. The name leads because that is what you recognise; the countdown is
 * the detail, and it is relative because "in 5 days" is what you act on.
 */
@Composable
private fun ComingUp(
    upcoming: List<UpcomingEvent>,
    m: DayMetrics,
    modifier: Modifier = Modifier,
) {
    if (upcoming.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Rule()
        Text("COMING UP", style = smallCaps(m.categoryLabelSize, Paper.inkLabel))
        FadingColumn(
            modifier = Modifier.heightIn(max = m.upcomingMaxHeight),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            upcoming.forEach { next ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CategoryIcon(
                        category = next.event.category,
                        tint = accentFor(next.event.category),
                        size = m.upcomingIconSize,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = next.event.name,
                            style = body(m.secondarySize, Paper.inkSecondary),
                        )
                        Text(
                            text = countdownLabel(next.daysAway),
                            style = body(m.metaSize, Paper.inkTertiary),
                        )
                    }
                }
            }
        }
    }
}

/**
 * A scrolling column that says so.
 *
 * Both scroll areas here are bounded, and both engage rarely — one day in a
 * hundred on a desktop, one in twelve on a phone. Rare is what makes this
 * necessary rather than optional: met once or twice a year with no prior
 * experience of this app scrolling, hidden rows read as missing events, which
 * is the failure removing the count cap was meant to prevent.
 *
 * A fade rather than a scrollbar: it works on every platform without an
 * expect/actual seam, and a page softening at the fold suits the paper better
 * than a stock control would. Bottom edge only — the header above already
 * marks where the list starts.
 */
@Composable
private fun FadingColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    Box(modifier) {
        Column(
            modifier = Modifier.verticalScroll(scroll),
            verticalArrangement = verticalArrangement,
            content = content,
        )
        if (scroll.canScrollForward) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(FADE_HEIGHT)
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Paper.ground)),
                    ),
            )
        }
    }
}

/** Enough to read as a soft edge rather than a band. */
private val FADE_HEIGHT = 28.dp

@Composable
private fun Rule() {
    Spacer(Modifier.fillMaxWidth().height(1.dp).background(Paper.rule))
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
