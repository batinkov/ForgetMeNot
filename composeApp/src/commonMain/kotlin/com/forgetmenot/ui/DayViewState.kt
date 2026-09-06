package com.forgetmenot.ui

import com.forgetmenot.data.EventRepository
import com.forgetmenot.domain.ReminderEvent
import com.forgetmenot.domain.on
import com.forgetmenot.domain.upcoming
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

/**
 * Holds what the day view shows and how it changes.
 *
 * This exists because reading events is asynchronous and can fail, which
 * `remember { }` cannot express. It keeps the full event list privately and
 * publishes only the day in question.
 */
class DayViewState(
    repository: EventRepository,
    scope: CoroutineScope,
    private val zone: TimeZone = TimeZone.currentSystemDefault(),
    private val rollover: Boolean = true,
    private val now: () -> Instant,
) {
    private fun today(): LocalDate = now().toLocalDateTime(zone).date

    /** What the view currently believes "today" is, so it can notice when that changes. */
    private var believedToday: LocalDate = today()
    private var all: List<ReminderEvent> = emptyList()

    private val _state = MutableStateFlow(DayUiState(date = today()))
    val state: StateFlow<DayUiState> = _state.asStateFlow()

    init {
        // One wake a night. The trigger that matters is regaining focus, since
        // staleness is only visible to someone looking; this covers the one case
        // focus cannot — a window left focused and untouched across midnight.
        // Suspending through it makes the delay fire late, by which point focus
        // has already corrected the date and this is a harmless no-op.
        if (rollover) {
            scope.launch {
                while (isActive) {
                    delay(untilNextMidnight())
                    refreshToday()
                }
            }
        }
        scope.launch {
            repository.events()
                .catch { cause ->
                    _state.update {
                        it.copy(load = LoadState.Failed(cause.message ?: "unreadable"))
                    }
                }
                .collect { loaded ->
                    all = loaded
                    _state.update {
                        it.copy(
                            events = all.on(it.date),
                            upcoming = all.upcoming(it.date),
                            load = LoadState.Ready,
                        )
                    }
                }
        }
    }

    /** Done is per-day, so moving the date clears it — the correct lifetime for it. */
    fun showDate(date: LocalDate) {
        _state.update {
            it.copy(
                date = date,
                events = all.on(date),
                upcoming = all.upcoming(date),
                done = emptySet(),
            )
        }
    }

    fun showPreviousDay() = showDate(_state.value.date.minus(1, DateTimeUnit.DAY))

    /**
     * Back to the real today, read from the clock rather than remembered from
     * startup — an app left open overnight would otherwise return to yesterday.
     */
    fun showToday() = showDate(today())

    /**
     * Moves the view onto the new day when the date has changed underneath it —
     * but only if it was still sitting on today. Someone who has navigated
     * elsewhere should not be yanked back at midnight.
     */
    fun refreshToday() {
        val now = today()
        if (now == believedToday) return
        val wasShowingToday = _state.value.date == believedToday
        believedToday = now
        if (wasShowingToday) showDate(now)
    }

    fun showNextDay() = showDate(_state.value.date.plus(1, DateTimeUnit.DAY))

    /** Most days are empty; without these, finding one to look at means a lot of clicking. */
    fun showNextDayWithEvents() = showNearestDayWithEvents(step = 1)

    fun showPreviousDayWithEvents() = showNearestDayWithEvents(step = -1)

    /** Walks a whole year in [step]-sized hops and stops on the first day that has something. */
    private fun showNearestDayWithEvents(step: Int) {
        var date = _state.value.date.plus(step, DateTimeUnit.DAY)
        repeat(DAYS_IN_LEAP_YEAR) {
            if (all.on(date).isNotEmpty()) {
                showDate(date)
                return
            }
            date = date.plus(step, DateTimeUnit.DAY)
        }
    }

    fun toggleDone(event: ReminderEvent) {
        _state.update {
            it.copy(done = if (event in it.done) it.done - event else it.done + event)
        }
    }

    internal fun untilNextMidnight(): Duration {
        val instant = now()
        val nextMidnight = instant.toLocalDateTime(zone).date
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(zone)
        // A second past, so the clock has certainly ticked over when we look.
        return (nextMidnight - instant) + ONE_SECOND
    }

    private companion object {
        const val DAYS_IN_LEAP_YEAR = 366
        val ONE_SECOND: Duration = kotlin.time.Duration.parse("1s")
    }
}
