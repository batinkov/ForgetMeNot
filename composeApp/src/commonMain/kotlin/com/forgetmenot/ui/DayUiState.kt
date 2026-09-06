package com.forgetmenot.ui

import com.forgetmenot.domain.ReminderEvent
import kotlinx.datetime.LocalDate

/** Everything the day view renders, and nothing it has to work out for itself. */
data class DayUiState(
    val date: LocalDate,
    val events: List<ReminderEvent> = emptyList(),
    val done: Set<ReminderEvent> = emptySet(),
    val load: LoadState = LoadState.Loading,
)

sealed interface LoadState {
    data object Loading : LoadState
    data object Ready : LoadState

    /**
     * The calendar could not be read. Worth its own state rather than falling
     * back to an empty day: an app that says "Nothing today" when it simply
     * failed to look is lying about the one thing it exists to tell you.
     */
    data class Failed(val message: String) : LoadState
}
