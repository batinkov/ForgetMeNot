package com.forgetmenot.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.forgetmenot.data.JsonEventRepository
import forgetmenot.composeapp.generated.resources.Res
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Wires the data source to the state holder. This is the only place that knows
 * events arrive as a bundled Compose resource — the repository is handed a way
 * to read bytes and never learns where they came from.
 */
@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val day = remember {
        DayViewState(
            repository = JsonEventRepository(readBytes = { path -> Res.readBytes(path) }),
            scope = scope,
            initialDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        )
    }
    val state by day.state.collectAsState()

    MaterialTheme {
        DayView(
            state = state,
            onPreviousDay = day::showPreviousDay,
            onNextDay = day::showNextDay,
            onNextEventDay = day::showNextDayWithEvents,
            onToggleDone = day::toggleDone,
        )
    }
}
