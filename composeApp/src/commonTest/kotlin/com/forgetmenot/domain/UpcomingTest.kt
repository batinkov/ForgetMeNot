package com.forgetmenot.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class UpcomingTest {

    private fun event(name: String, month: Int, day: Int) =
        ReminderEvent(name, month = month, day = day, category = EventCategory.BIRTHDAY)

    private val calendar = listOf(
        event("today", 6, 1),
        event("tomorrow", 6, 2),
        event("in a week", 6, 8),
        event("in three weeks", 6, 22),
    )

    private val june1 = LocalDate(2026, 6, 1)

    @Test
    fun theNearestComeFirst() {
        assertEquals(
            listOf("tomorrow", "in a week"),
            calendar.upcoming(june1).map { it.event.name },
        )
    }

    @Test
    fun todayIsExcludedBecauseItIsAlreadyACardAbove() {
        assertEquals(false, calendar.upcoming(june1).any { it.event.name == "today" })
    }

    @Test
    fun theWindowIsTheOnlyLimit() {
        // Three weeks out is beyond a fortnight, and comes back when the window widens.
        assertEquals(false, calendar.upcoming(june1).any { it.event.name == "in three weeks" })
        assertEquals(
            true,
            calendar.upcoming(june1, withinDays = 30).any { it.event.name == "in three weeks" },
        )
    }

    @Test
    fun everythingInTheWindowIsListedHoweverManyThereAre() {
        // A crowded day must not push later events out: nothing is silently dropped,
        // the section bounds its own height and scrolls instead.
        val crowded = listOf(
            event("a", 6, 5), event("b", 6, 5), event("c", 6, 5),
            event("d", 6, 5), event("e", 6, 5), event("f", 6, 12),
        )
        assertEquals(6, crowded.upcoming(june1).size)
        assertEquals("f", crowded.upcoming(june1).last().event.name)
    }

    @Test
    fun daysAwayIsCounted() {
        assertEquals(listOf(1, 7), calendar.upcoming(june1).map { it.daysAway })
    }

    @Test
    fun theWindowCrossesTheYearBoundary() {
        val newYear = listOf(event("in January", 1, 5))
        assertEquals(listOf(16), newYear.upcoming(LocalDate(2026, 12, 20), withinDays = 20).map { it.daysAway })
    }

    @Test
    fun anEmptyStretchYieldsNothing() {
        assertEquals(emptyList(), listOf(event("far", 12, 25)).upcoming(june1))
    }

    @Test
    fun theCountdownIsAlwaysCountedNeverNamed() {
        // "Tomorrow" would be relative to the real today, not the day on screen.
        assertEquals("In 1 day", countdownLabel(1))
        assertEquals("In 5 days", countdownLabel(5))
    }
}
