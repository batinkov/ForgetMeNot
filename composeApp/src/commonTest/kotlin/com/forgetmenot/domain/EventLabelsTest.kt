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
    fun publicCategoriesNeverShowANumber() {
        assertNull(event(EventCategory.NAMEDAY, 1900).countLabel(day))
        assertNull(event(EventCategory.FEAST, 1900).countLabel(day))
        assertNull(event(EventCategory.NATIONAL_DAY, 1878).countLabel(day))
    }

    @Test
    fun noNumberWithoutAnOriginalYear() {
        assertNull(event(EventCategory.BIRTHDAY).countLabel(day))
    }

    @Test
    fun theSecondaryLineFallsBackToTheNote() {
        val nameday = event(EventCategory.NAMEDAY, note = "Atanas · Nasko")
        assertEquals("Atanas · Nasko", nameday.secondaryLine(day))
    }

    @Test
    fun theNumberWinsOverTheNoteWhenBothExist() {
        val birthday = event(EventCategory.BIRTHDAY, originalYear = 1992, note = "buy flowers")
        assertEquals("Turning 34", birthday.secondaryLine(day))
    }

    @Test
    fun aBareEventHasNoSecondaryLine() {
        assertNull(event(EventCategory.CUSTOM).secondaryLine(day))
    }
}
