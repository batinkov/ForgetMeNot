package com.forgetmenot.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The query the day view is built on: what falls on a given date. */
class DayEventsTest {

    private fun event(name: String, month: Int, day: Int, category: EventCategory = EventCategory.BIRTHDAY) =
        ReminderEvent(name, month = month, day = day, category = category)

    @Test
    fun eventOccursOnItsOwnDayOnly() {
        val e = event("Maria", 1, 18)
        assertTrue(e.occursOn(LocalDate(2026, 1, 18)))
        assertFalse(e.occursOn(LocalDate(2026, 1, 17)))
        assertFalse(e.occursOn(LocalDate(2026, 2, 18)))
    }

    @Test
    fun eventOccursEveryYear() {
        val e = event("Maria", 1, 18)
        assertTrue(e.occursOn(LocalDate(2026, 1, 18)))
        assertTrue(e.occursOn(LocalDate(2031, 1, 18)))
    }

    @Test
    fun feb29EventLandsOnFeb28InACommonYear() {
        val e = event("Ivan", 2, 29)
        assertTrue(e.occursOn(LocalDate(2027, 2, 28)))
        assertFalse(e.occursOn(LocalDate(2027, 3, 1)))
    }

    @Test
    fun feb29EventStaysOnFeb29InALeapYear() {
        val e = event("Ivan", 2, 29)
        assertTrue(e.occursOn(LocalDate(2028, 2, 29)))
        assertFalse(e.occursOn(LocalDate(2028, 2, 28)))
    }

    @Test
    fun aDayCanHoldSeveralEventsAndKeepsTheirOrder() {
        val events = listOf(
            event("Atanasovden", 1, 18, EventCategory.NAMEDAY),
            event("Mum", 3, 14),
            event("Maria", 1, 18),
        )
        assertEquals(listOf("Atanasovden", "Maria"), events.on(LocalDate(2026, 1, 18)).map { it.name })
    }

    @Test
    fun aDayWithNothingOnItIsEmpty() {
        val events = listOf(event("Mum", 3, 14))
        assertTrue(events.on(LocalDate(2026, 3, 15)).isEmpty())
    }
}
