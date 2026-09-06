package com.forgetmenot.domain

import kotlinx.datetime.LocalDate

/** An event that hasn't happened yet, and how long you have. */
data class UpcomingEvent(val event: ReminderEvent, val daysAway: Int)

/**
 * A fortnight is what counts as "soon". The window is the only limit: everything
 * inside it is listed, however many that turns out to be, so nothing is silently
 * dropped. The section bounds its own height and scrolls instead.
 */
const val UPCOMING_WINDOW_DAYS: Int = 14

/**
 * The next few events after [from], nearest first.
 *
 * Strictly after: anything falling on [from] is already a card above, and
 * showing it twice would make the day look busier than it is.
 */
fun List<ReminderEvent>.upcoming(
    from: LocalDate,
    withinDays: Int = UPCOMING_WINDOW_DAYS,
): List<UpcomingEvent> =
    mapNotNull { event ->
        event.daysUntil(from).takeIf { it in 1..withinDays }?.let { UpcomingEvent(event, it) }
    }.sortedBy { it.daysAway }
