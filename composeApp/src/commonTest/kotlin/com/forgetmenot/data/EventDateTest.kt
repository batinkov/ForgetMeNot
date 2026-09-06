package com.forgetmenot.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EventDateTest {

    @Test
    fun aFullDateCarriesItsYear() {
        assertEquals(EventDate(month = 11, day = 11, year = 1983), parseEventDate("1983-11-11"))
    }

    @Test
    fun theVCardFormMeansTheYearIsUnknown() {
        assertEquals(EventDate(month = 1, day = 6, year = null), parseEventDate("--01-06"))
    }

    @Test
    fun surroundingWhitespaceIsTolerated() {
        assertEquals(EventDate(month = 1, day = 6, year = null), parseEventDate("  --01-06 "))
    }

    @Test
    fun februaryTwentyNineIsAllowed() {
        // A leap-day event is legitimate; Recurrence clamps it in common years.
        assertEquals(EventDate(month = 2, day = 29, year = 1996), parseEventDate("1996-02-29"))
    }

    @Test
    fun aDayBeyondTheMonthIsRejected() {
        assertFailsWith<IllegalArgumentException> { parseEventDate("--02-30") }
        assertFailsWith<IllegalArgumentException> { parseEventDate("--04-31") }
        assertFailsWith<IllegalArgumentException> { parseEventDate("1983-11-32") }
    }

    @Test
    fun aMonthOutsideTheYearIsRejected() {
        assertFailsWith<IllegalArgumentException> { parseEventDate("--13-01") }
        assertFailsWith<IllegalArgumentException> { parseEventDate("--00-01") }
    }

    @Test
    fun anythingThatIsNotOneOfTheTwoFormsIsRejected() {
        assertFailsWith<IllegalArgumentException> { parseEventDate("11-11") }
        assertFailsWith<IllegalArgumentException> { parseEventDate("1983/11/11") }
        assertFailsWith<IllegalArgumentException> { parseEventDate("") }
        assertFailsWith<IllegalArgumentException> { parseEventDate("--1-6") }
    }

    @Test
    fun theOffendingTextIsInTheMessageSoABadRowCanBeFound() {
        val message = assertFailsWith<IllegalArgumentException> { parseEventDate("--02-30") }.message
        assertEquals(true, message?.contains("--02-30"))
    }
}
