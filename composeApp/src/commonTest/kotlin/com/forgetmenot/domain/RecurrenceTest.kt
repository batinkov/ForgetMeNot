package com.forgetmenot.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class RecurrenceTest {

    private fun event(month: Int, day: Int, originalYear: Int? = null) =
        ReminderEvent("t", month = month, day = day, category = EventCategory.BIRTHDAY, originalYear = originalYear)

    @Test
    fun occurrenceLaterThisYearIsUsed() {
        val e = event(12, 25)
        assertEquals(LocalDate(2026, 12, 25), e.nextOccurrence(LocalDate(2026, 7, 18)))
    }

    @Test
    fun occurrenceAlreadyPassedRollsToNextYear() {
        val e = event(3, 14)
        assertEquals(LocalDate(2027, 3, 14), e.nextOccurrence(LocalDate(2026, 7, 18)))
    }

    @Test
    fun eventTodayReturnsTodayWithZeroDays() {
        val e = event(7, 18)
        val today = LocalDate(2026, 7, 18)
        assertEquals(today, e.nextOccurrence(today))
        assertEquals(0, e.daysUntil(today))
    }

    @Test
    fun feb29ClampsToFeb28InCommonYear() {
        val e = event(2, 29)
        // 2027 is not a leap year -> clamps to Feb 28.
        assertEquals(LocalDate(2027, 2, 28), e.nextOccurrence(LocalDate(2026, 7, 18)))
    }

    @Test
    fun feb29StaysFeb29InLeapYear() {
        val e = event(2, 29)
        // 2028 is a leap year.
        assertEquals(LocalDate(2028, 2, 29), e.nextOccurrence(LocalDate(2027, 6, 1)))
    }

    @Test
    fun yearsAtNextOccurrenceComputesAge() {
        val e = event(3, 14, originalYear = 1958)
        // Next birthday is in 2027 -> turning 69.
        assertEquals(69, e.yearsAtNextOccurrence(LocalDate(2026, 7, 18)))
    }

    @Test
    fun yearsIsNullWhenOriginalYearUnknown() {
        val e = event(12, 2)
        assertEquals(null, e.yearsAtNextOccurrence(LocalDate(2026, 7, 18)))
    }
}
