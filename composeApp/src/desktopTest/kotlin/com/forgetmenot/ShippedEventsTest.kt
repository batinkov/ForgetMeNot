package com.forgetmenot

import com.forgetmenot.data.decodeEvents
import com.forgetmenot.domain.ReminderEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Checks the calendar we actually ship, rather than a hand-written fixture.
 *
 * This is deliberately here instead of a JSON schema: it catches everything a
 * schema would (shape, types, unknown categories, impossible dates) plus the
 * semantic things a schema cannot express, needs no extra dependency, and runs
 * in `make test`.
 */
class ShippedEventsTest {

    private fun shipped(): List<ReminderEvent> {
        val candidates = listOf(
            "src/commonMain/composeResources/files/events.json",
            "composeApp/src/commonMain/composeResources/files/events.json",
        )
        val file = candidates.map(::File).firstOrNull { it.exists() }
            ?: error("events.json not found from ${File(".").absolutePath}")
        return decodeEvents(file.readText())
    }

    @Test
    fun theShippedCalendarParses() {
        assertTrue(shipped().isNotEmpty(), "the shipped calendar is empty")
    }

    @Test
    fun noTwoEventsAreIndistinguishable() {
        // Identity is name + date + category, so a duplicate would render twice
        // and, once done-state is persisted, be impossible to tell apart.
        val events = shipped()
        val distinct = events.map { Triple(it.name, it.month to it.day, it.category) }.toSet()
        assertEquals(events.size, distinct.size, "duplicate rows in events.json")
    }

    @Test
    fun noEventClaimsToHaveStartedInTheFuture() {
        val thisYear = Clock.System.todayIn(TimeZone.currentSystemDefault()).year
        val ahead = shipped().filter { (it.originalYear ?: 0) > thisYear }
        assertEquals(emptyList(), ahead.map { "${it.name} (${it.originalYear})" })
    }

    @Test
    fun everyEventHasAName() {
        assertEquals(emptyList(), shipped().filter { it.name.isBlank() })
    }
}
