package com.forgetmenot.domain

import kotlinx.datetime.LocalDate

/** The small caps label above the name. */
fun EventCategory.label(): String = when (this) {
    EventCategory.BIRTHDAY -> "Birthday"
    EventCategory.NAMEDAY -> "Nameday"
    EventCategory.ANNIVERSARY -> "Anniversary"
    EventCategory.MEMORIAL -> "In memory"
    EventCategory.FEAST -> "Feast"
    EventCategory.NATIONAL_DAY -> "National day"
    EventCategory.CUSTOM -> "Custom"
}

/**
 * The number a card shows, worded for its category — an age, an ordinal, years
 * elapsed. Null only when there is no year to count from: an event with a known
 * origin always says something about it, and one without simply stays quiet.
 * The wording matters more than the number — "turning 12" would be grotesque on
 * a memorial, and a birthday does not have an anniversary.
 */
fun ReminderEvent.countLabel(on: LocalDate): String? {
    val years = originalYear?.let { on.year - it } ?: return null
    return when (category) {
        EventCategory.BIRTHDAY -> "Turning $years"
        EventCategory.ANNIVERSARY -> "$years${ordinal(years)} anniversary"
        EventCategory.MEMORIAL -> "$years years since"
        EventCategory.NAMEDAY, EventCategory.FEAST,
        EventCategory.NATIONAL_DAY, EventCategory.CUSTOM -> "$years years"
    }
}

/**
 * How long you have, counted from the day being shown rather than from the real
 * today. Deliberately never "Tomorrow": that word is anchored to the present, so
 * on any day but today it names the wrong date.
 */
fun countdownLabel(daysAway: Int): String =
    if (daysAway == 1) "In 1 day" else "In $daysAway days"

private fun ordinal(n: Int): String {
    if (n % 100 in 11..13) return "th"
    return when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}
