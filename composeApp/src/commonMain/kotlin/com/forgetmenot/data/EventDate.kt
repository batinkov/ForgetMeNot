package com.forgetmenot.data

/**
 * A date as written in events.json: either a full ISO date when the origin year
 * is known ("1983-11-11"), or the vCard form for a recurring date whose year is
 * unknown or meaningless ("--01-06").
 */
internal data class EventDate(val month: Int, val day: Int, val year: Int?)

private val WITH_YEAR = Regex("""^(\d{4})-(\d{2})-(\d{2})$""")
private val WITHOUT_YEAR = Regex("""^--(\d{2})-(\d{2})$""")

/** Longest possible day per month. February allows 29: a leap-day event is legitimate. */
private val LAST_DAY = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

/** @throws IllegalArgumentException with the offending text, so a bad row is findable. */
internal fun parseEventDate(raw: String): EventDate {
    val text = raw.trim()

    WITHOUT_YEAR.matchEntire(text)?.destructured?.let { (m, d) ->
        return checked(EventDate(m.toInt(), d.toInt(), year = null), text)
    }
    WITH_YEAR.matchEntire(text)?.destructured?.let { (y, m, d) ->
        return checked(EventDate(m.toInt(), d.toInt(), year = y.toInt()), text)
    }
    throw IllegalArgumentException("""not a date: "$raw" (expected "1983-11-11" or "--01-06")""")
}

private fun checked(date: EventDate, raw: String): EventDate {
    require(date.month in 1..12) { """month out of range in "$raw"""" }
    require(date.day in 1..LAST_DAY[date.month - 1]) { """day out of range in "$raw"""" }
    return date
}
