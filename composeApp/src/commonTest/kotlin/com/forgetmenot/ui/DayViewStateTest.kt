package com.forgetmenot.ui

import com.forgetmenot.data.EventRepository
import com.forgetmenot.domain.EventCategory
import com.forgetmenot.domain.ReminderEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DayViewStateTest {

    private fun event(name: String, month: Int, day: Int) =
        ReminderEvent(name, month = month, day = day, category = EventCategory.BIRTHDAY)

    private val calendar = listOf(
        event("March", 3, 14),
        event("June", 6, 29),
        event("October", 10, 5),
    )

    /** Unconfined runs the collect eagerly, so the state is loaded before this returns. */
    private fun holder(
        events: List<ReminderEvent> = calendar,
        on: LocalDate = LocalDate(2026, 6, 1),
        repository: EventRepository = object : EventRepository {
            override fun events(): Flow<List<ReminderEvent>> = flowOf(events)
        },
    ) = DayViewState(repository, CoroutineScope(Dispatchers.Unconfined)) { on }

    @Test
    fun aLoadedCalendarLeavesTheViewReady() {
        assertEquals(LoadState.Ready, holder().state.value.load)
    }

    @Test
    fun aFailedReadIsReportedRatherThanLookingLikeAnEmptyDay() {
        val broken = object : EventRepository {
            override fun events(): Flow<List<ReminderEvent>> = flow { throw IllegalStateException("no file") }
        }
        val load = holder(repository = broken).state.value.load
        assertTrue(load is LoadState.Failed, "expected Failed, was $load")
        assertEquals("no file", (load as LoadState.Failed).message)
    }

    @Test
    fun onlyTheShownDaysEventsAreExposed() {
        val day = holder(on = LocalDate(2026, 6, 29))
        assertEquals(listOf("June"), day.state.value.events.map { it.name })
    }

    @Test
    fun nextEventSkipsEveryEmptyDayBetween() {
        val day = holder(on = LocalDate(2026, 6, 1))
        day.showNextDayWithEvents()
        assertEquals(LocalDate(2026, 6, 29), day.state.value.date)
    }

    @Test
    fun previousEventScansBackwards() {
        val day = holder(on = LocalDate(2026, 6, 1))
        day.showPreviousDayWithEvents()
        assertEquals(LocalDate(2026, 3, 14), day.state.value.date)
    }

    @Test
    fun theTwoDirectionsAreInverses() {
        val day = holder(on = LocalDate(2026, 6, 29))
        day.showNextDayWithEvents()
        assertEquals(LocalDate(2026, 10, 5), day.state.value.date)
        day.showPreviousDayWithEvents()
        assertEquals(LocalDate(2026, 6, 29), day.state.value.date)
    }

    @Test
    fun anEmptyCalendarLeavesTheDateAloneRatherThanSpinning() {
        val day = holder(events = emptyList(), on = LocalDate(2026, 6, 1))
        day.showNextDayWithEvents()
        day.showPreviousDayWithEvents()
        assertEquals(LocalDate(2026, 6, 1), day.state.value.date)
    }

    @Test
    fun theViewOpensOnToday() {
        assertEquals(LocalDate(2026, 6, 1), holder().state.value.date)
    }

    @Test
    fun todayReturnsFromWhereverYouHaveWandered() {
        val day = holder(on = LocalDate(2026, 6, 1))
        day.showPreviousDayWithEvents()
        assertEquals(LocalDate(2026, 3, 14), day.state.value.date)
        day.showToday()
        assertEquals(LocalDate(2026, 6, 1), day.state.value.date)
    }

    @Test
    fun todayReadsTheClockRatherThanRememberingStartup() {
        // An app left open overnight must not treat yesterday as today.
        var now = LocalDate(2026, 6, 1)
        val day = DayViewState(
            repository = object : EventRepository {
                override fun events(): Flow<List<ReminderEvent>> = flowOf(calendar)
            },
            scope = CoroutineScope(Dispatchers.Unconfined),
        ) { now }

        assertEquals(LocalDate(2026, 6, 1), day.state.value.date)
        now = LocalDate(2026, 6, 2)
        day.showToday()
        assertEquals(LocalDate(2026, 6, 2), day.state.value.date)
    }

    @Test
    fun doneIsRememberedWithinADay() {
        val day = holder(on = LocalDate(2026, 6, 29))
        val june = day.state.value.events.single()
        day.toggleDone(june)
        assertEquals(setOf(june), day.state.value.done)
        day.toggleDone(june)
        assertEquals(emptySet(), day.state.value.done)
    }

    @Test
    fun doneClearsWhenTheDayChanges() {
        val day = holder(on = LocalDate(2026, 6, 29))
        day.toggleDone(day.state.value.events.single())
        day.showNextDayWithEvents()
        assertEquals(emptySet(), day.state.value.done)
    }
}
