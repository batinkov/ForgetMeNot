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
 * elapsed. Null when there is no year to count from, or when the category
 * simply doesn't have a number: a feast day is not in its 1699th year to
 * anyone who cares about it, and "turning 12" would be grotesque on a memorial.
 */
fun ReminderEvent.countLabel(on: LocalDate): String? {
    val years = originalYear?.let { on.year - it } ?: return null
    return when (category) {
        EventCategory.BIRTHDAY -> "Turning $years"
        EventCategory.ANNIVERSARY -> "$years${ordinal(years)} anniversary"
        EventCategory.MEMORIAL -> "$years years since"
        EventCategory.CUSTOM -> "$years years"
        EventCategory.NAMEDAY, EventCategory.FEAST, EventCategory.NATIONAL_DAY -> null
    }
}

/** The one secondary line under a name: the number if there is one, else the note. */
fun ReminderEvent.secondaryLine(on: LocalDate): String? =
    countLabel(on) ?: note.takeIf { it.isNotBlank() }

private fun ordinal(n: Int): String {
    if (n % 100 in 11..13) return "th"
    return when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}
