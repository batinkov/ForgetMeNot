package com.forgetmenot.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalWindowInfo
import kotlinx.coroutines.flow.filter
import com.forgetmenot.data.JsonEventRepository
import forgetmenot.composeapp.generated.resources.Res
import kotlinx.datetime.Clock

/**
 * Wires the data source to the state holder. This is the only place that knows
 * events arrive as a bundled Compose resource — the repository is handed a way
 * to read bytes and never learns where they came from.
 */
@Composable
fun App(showDevTools: Boolean) {
    val scope = rememberCoroutineScope()
    val day = remember {
        DayViewState(
            repository = JsonEventRepository(readBytes = { path -> Res.readBytes(path) }),
            scope = scope,
            now = { Clock.System.now() },
        )
    }
    val state by day.state.collectAsState()

    // The moment the date being wrong becomes visible is the moment someone looks.
    // isWindowFocused is Compose UI, not a desktop API, so this is the same
    // mechanism on Android and iOS: returning to the app re-reads the clock.
    val windowInfo = LocalWindowInfo.current
    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }
            .filter { it }
            .collect { day.refreshToday() }
    }

    MaterialTheme {
        DayView(
            state = state,
            onPreviousDay = day::showPreviousDay,
            onNextDay = day::showNextDay,
            onNextEventDay = day::showNextDayWithEvents,
            onPreviousEventDay = day::showPreviousDayWithEvents,
            onToday = day::showToday,
            onToggleDone = day::toggleDone,
            showDevTools = showDevTools,
        )
    }
}
