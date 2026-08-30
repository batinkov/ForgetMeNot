package com.forgetmenot.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

/** Number of days in the given month, accounting for leap years. */
private fun daysInMonth(year: Int, month: Int): Int {
    val firstOfThis = LocalDate(year, month, 1)
    val firstOfNext = if (month == 12) LocalDate(year + 1, 1, 1) else LocalDate(year, month + 1, 1)
    return firstOfThis.daysUntil(firstOfNext)
}

/**
 * A Feb-29 event still needs a day to land on in common years. We clamp to the
 * last valid day of the month (Feb 28), which is the usual convention.
 */
private fun clampDay(year: Int, month: Int, day: Int): Int =
    minOf(day, daysInMonth(year, month))

/** Whether this event falls on [date] — the query the day view is built on. */
fun ReminderEvent.occursOn(date: LocalDate): Boolean =
    date.monthNumber == month && date.dayOfMonth == clampDay(date.year, month, day)

/** Every event falling on [date], in the order they were given. */
fun List<ReminderEvent>.on(date: LocalDate): List<ReminderEvent> =
    filter { it.occursOn(date) }

/**
 * The next date this event occurs on or after [from]. If today is the event
 * day, today is returned (0 days until).
 */
fun ReminderEvent.nextOccurrence(from: LocalDate): LocalDate {
    val thisYear = LocalDate(from.year, month, clampDay(from.year, month, day))
    return if (thisYear >= from) {
        thisYear
    } else {
        val ny = from.year + 1
        LocalDate(ny, month, clampDay(ny, month, day))
    }
}

/** Whole days from [from] until the next occurrence (0 == today). */
fun ReminderEvent.daysUntil(from: LocalDate): Int =
    from.daysUntil(nextOccurrence(from))

/**
 * How many years the event will have counted at its next occurrence — the age
 * on the next birthday, the anniversary number, etc. Null when [originalYear]
 * is unknown.
 */
fun ReminderEvent.yearsAtNextOccurrence(from: LocalDate): Int? =
    originalYear?.let { nextOccurrence(from).year - it }
