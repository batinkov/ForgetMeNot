package com.forgetmenot.ui

import com.forgetmenot.data.EventRepository
import com.forgetmenot.domain.ReminderEvent
import com.forgetmenot.domain.on
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

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
    initialDate: LocalDate,
) {
    private var all: List<ReminderEvent> = emptyList()

    private val _state = MutableStateFlow(DayUiState(date = initialDate))
    val state: StateFlow<DayUiState> = _state.asStateFlow()

    init {
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
                        it.copy(events = all.on(it.date), load = LoadState.Ready)
                    }
                }
        }
    }

    /** Done is per-day, so moving the date clears it — the correct lifetime for it. */
    fun showDate(date: LocalDate) {
        _state.update { it.copy(date = date, events = all.on(date), done = emptySet()) }
    }

    fun showPreviousDay() = showDate(_state.value.date.minus(1, DateTimeUnit.DAY))

    fun showNextDay() = showDate(_state.value.date.plus(1, DateTimeUnit.DAY))

    /** Most days are empty; without this, finding one to look at means a lot of clicking. */
    fun showNextDayWithEvents() {
        var date = _state.value.date.plus(1, DateTimeUnit.DAY)
        repeat(DAYS_IN_LEAP_YEAR) {
            if (all.on(date).isNotEmpty()) {
                showDate(date)
                return
            }
            date = date.plus(1, DateTimeUnit.DAY)
        }
    }

    fun toggleDone(event: ReminderEvent) {
        _state.update {
            it.copy(done = if (event in it.done) it.done - event else it.done + event)
        }
    }

    private companion object {
        const val DAYS_IN_LEAP_YEAR = 366
    }
}
