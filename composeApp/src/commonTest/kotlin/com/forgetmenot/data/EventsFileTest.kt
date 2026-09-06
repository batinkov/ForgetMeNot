package com.forgetmenot.data

import com.forgetmenot.domain.EventCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EventsFileTest {

    private fun file(events: String, version: Int = 1) =
        """{ "version": $version, "events": [$events] }"""

    @Test
    fun aRowBecomesADomainEvent() {
        val events = decodeEvents(
            file("""{ "date": "1962-06-27", "category": "BIRTHDAY", "name": "Мина" }"""),
        )
        assertEquals(1, events.size)
        with(events.single()) {
            assertEquals("Мина", name)
            assertEquals(6, month)
            assertEquals(27, day)
            assertEquals(1962, originalYear)
            assertEquals(EventCategory.BIRTHDAY, category)
            assertEquals("", note)
        }
    }

    @Test
    fun aYearlessRowHasNoOriginalYear() {
        val events = decodeEvents(
            file("""{ "date": "--01-06", "category": "NAMEDAY", "name": "Йордановден" }"""),
        )
        assertEquals(null, events.single().originalYear)
    }

    @Test
    fun theNoteIsCarriedThroughWhenPresent() {
        val events = decodeEvents(
            file("""{ "date": "--01-06", "category": "NAMEDAY", "name": "x", "note": "Богоявление" }"""),
        )
        assertEquals("Богоявление", events.single().note)
    }

    @Test
    fun anUnknownCategoryFailsTheFileRatherThanSkippingTheRow() {
        // Silently dropping an event is the one failure this app must not have.
        assertFailsWith<IllegalArgumentException> {
            decodeEvents(file("""{ "date": "--01-06", "category": "PARTY", "name": "x" }"""))
        }
    }

    @Test
    fun aBadDateFailsTheFile() {
        assertFailsWith<IllegalArgumentException> {
            decodeEvents(file("""{ "date": "sometime", "category": "FEAST", "name": "x" }"""))
        }
    }

    @Test
    fun anUnsupportedVersionIsRefused() {
        val thrown = assertFailsWith<IllegalArgumentException> {
            decodeEvents(file("""{ "date": "--01-06", "category": "FEAST", "name": "x" }""", version = 2))
        }
        assertEquals(true, thrown.message?.contains("version 2"))
    }

    @Test
    fun unknownKeysAreToleratedSoANewerFileStaysReadable() {
        val events = decodeEvents(
            file("""{ "date": "--01-06", "category": "FEAST", "name": "x", "colour": "red" }"""),
        )
        assertEquals(1, events.size)
    }

    @Test
    fun anEmptyFileIsValidAndYieldsNothing() {
        assertEquals(emptyList(), decodeEvents(file("")))
    }
}
