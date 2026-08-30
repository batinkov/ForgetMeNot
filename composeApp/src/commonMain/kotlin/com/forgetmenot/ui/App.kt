package com.forgetmenot.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.forgetmenot.data.SampleEvents
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * The date is held here rather than read inside the day view, so the view is
 * testable and so a midnight rollover has one place to write to when we add it.
 */
@Composable
fun App() {
    var today by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }

    MaterialTheme {
        DayView(
            allEvents = SampleEvents.all,
            today = today,
            onDateChange = { today = it },
        )
    }
}
