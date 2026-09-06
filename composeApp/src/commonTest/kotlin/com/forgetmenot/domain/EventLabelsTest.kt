package com.forgetmenot.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** The number a card shows is worded per category — and sometimes withheld. */
class EventLabelsTest {

    private val day = LocalDate(2026, 1, 18)

    private fun event(category: EventCategory, originalYear: Int? = null, note: String = "") =
        ReminderEvent("x", month = 1, day = 18, category = category, originalYear = originalYear, note = note)

    @Test
    fun birthdayCountsAnAge() {
        assertEquals("Turning 34", event(EventCategory.BIRTHDAY, 1992).countLabel(day))
    }

    @Test
    fun anniversaryCountsAnOrdinal() {
        assertEquals("12th anniversary", event(EventCategory.ANNIVERSARY, 2014).countLabel(day))
        assertEquals("1st anniversary", event(EventCategory.ANNIVERSARY, 2025).countLabel(day))
        assertEquals("22nd anniversary", event(EventCategory.ANNIVERSARY, 2004).countLabel(day))
        assertEquals("23rd anniversary", event(EventCategory.ANNIVERSARY, 2003).countLabel(day))
    }

    @Test
    fun teensTakeThSuffix() {
        assertEquals("11th anniversary", event(EventCategory.ANNIVERSARY, 2015).countLabel(day))
        assertEquals("13th anniversary", event(EventCategory.ANNIVERSARY, 2013).countLabel(day))
    }

    @Test
    fun memorialCountsYearsSinceRatherThanAnAge() {
        assertEquals("12 years since", event(EventCategory.MEMORIAL, 2014).countLabel(day))
    }

    @Test
    fun everyOtherCategoryCountsElapsedYearsWhenItHasOne() {
        assertEquals("141 years", event(EventCategory.NATIONAL_DAY, 1885).countLabel(day))
        assertEquals("126 years", event(EventCategory.FEAST, 1900).countLabel(day))
        assertEquals("5 years", event(EventCategory.CUSTOM, 2021).countLabel(day))
    }

    @Test
    fun aCategoryWithNoYearInTheDataStaysQuiet() {
        // Namedays and most feasts have no origin year, so they say nothing.
        assertNull(event(EventCategory.NAMEDAY).countLabel(day))
        assertNull(event(EventCategory.FEAST).countLabel(day))
    }

    @Test
    fun noNumberWithoutAnOriginalYear() {
        assertNull(event(EventCategory.BIRTHDAY).countLabel(day))
    }

    @Test
    fun aNumberAndANoteCoexistRatherThanCompeting() {
        // Съединението has both; showing only the number would lose the better half.
        val unification = event(EventCategory.NATIONAL_DAY, 1885, note = "Източна Румелия…")
        assertEquals("141 years", unification.countLabel(day))
        assertEquals("Източна Румелия…", unification.note)
    }
}
