package com.forgetmenot.data

import com.forgetmenot.domain.EventCategory
import com.forgetmenot.domain.ReminderEvent

/**
 * Stand-in for the two databases we'll read later: personal events that never
 * change, and public ones regenerated per year. The field set is deliberately
 * the shape we expect the JSON to have, so swapping this for a file reader is
 * one function rather than a refactor.
 *
 * Dates here are sample data and worth checking before anyone relies on them.
 */
object SampleEvents {

    /** Hand-entered, permanent — these survive the turn of the year. */
    private val personal = listOf(
        ReminderEvent("Maria Ivanova", month = 1, day = 18, category = EventCategory.BIRTHDAY, originalYear = 1992),
        ReminderEvent("Dimitar Petrov", month = 1, day = 18, category = EventCategory.MEMORIAL, originalYear = 2014),
        ReminderEvent("Mum", month = 3, day = 14, category = EventCategory.BIRTHDAY, originalYear = 1958),
        ReminderEvent("Ivan Kolev", month = 2, day = 29, category = EventCategory.BIRTHDAY, originalYear = 1996),
        ReminderEvent("Elena & Stoyan", month = 7, day = 22, category = EventCategory.ANNIVERSARY, originalYear = 2016),
        ReminderEvent("Move-in day", month = 8, day = 1, category = EventCategory.CUSTOM, originalYear = 2021),
        ReminderEvent("Alex", month = 12, day = 2, category = EventCategory.BIRTHDAY),
    )

    /** Pulled from the calendar for the year — disposable, regenerated. */
    private val calendar = listOf(
        ReminderEvent(
            "Atanasovden", month = 1, day = 18, category = EventCategory.NAMEDAY,
            note = "Atanas · Nasko · Nadya · Tanya",
        ),
        ReminderEvent(
            "St Athanasius the Great", month = 1, day = 18, category = EventCategory.FEAST,
            note = "Patriarch of Alexandria, defender of the Nicene creed.",
        ),
        ReminderEvent(
            "Liberation Day", month = 3, day = 3, category = EventCategory.NATIONAL_DAY,
            note = "Public holiday",
        ),
        ReminderEvent(
            "Gergyovden", month = 5, day = 6, category = EventCategory.NAMEDAY,
            note = "Georgi · Gergana · Ganka",
        ),
        ReminderEvent(
            "Saints Cyril and Methodius", month = 5, day = 24, category = EventCategory.FEAST,
            note = "The Slavonic alphabet and the letters it gave away.",
        ),
        ReminderEvent(
            "Christmas", month = 12, day = 25, category = EventCategory.FEAST,
            note = "Public holiday",
        ),
    )

    /** What the day view reads. Two sources, one list — the view can't tell them apart. */
    val all: List<ReminderEvent> = personal + calendar
}
