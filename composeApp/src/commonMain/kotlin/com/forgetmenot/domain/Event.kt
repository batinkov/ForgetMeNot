package com.forgetmenot.domain

/**
 * The kind of event, which decides how a card reads: its accent, its icon and
 * the wording of its number (an age, an ordinal, years elapsed, or nothing).
 *
 * Personal categories are hand-entered and live in the permanent database;
 * public ones (nameday, feast, national day) are pulled from a calendar
 * regenerated each year. Nothing here encodes that split, because a category
 * is about how an event *reads*, not where it came from.
 */
enum class EventCategory { BIRTHDAY, NAMEDAY, ANNIVERSARY, MEMORIAL, FEAST, NATIONAL_DAY, CUSTOM }

/**
 * A yearly-recurring event, keyed by month/day rather than a full date so it
 * naturally repeats every year.
 *
 * There is deliberately no id: an event is identified by its name, date and
 * category. That holds only while "done" is per-session — a stored
 * acknowledgement would need something stable to point at.
 *
 * @param originalYear the year the event first occurred (birth year, wedding
 *   year, ...). Null when unknown — some people don't share their birth year.
 *   When present it lets us show "turning 30" / "5th anniversary".
 * @param note a short line shown under the name when there is no number to
 *   show: who celebrates a nameday, what a feast commemorates.
 */
data class ReminderEvent(
    val name: String,
    val month: Int,
    val day: Int,
    val category: EventCategory,
    val originalYear: Int? = null,
    val note: String = "",
)
