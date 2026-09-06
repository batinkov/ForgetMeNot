package com.forgetmenot.data

import com.forgetmenot.domain.EventCategory
import com.forgetmenot.domain.ReminderEvent
import kotlinx.serialization.Serializable

/**
 * The wire shape of events.json, kept separate from [ReminderEvent] so the
 * domain layer carries no serialization annotations and the file format can
 * change without the business rules noticing.
 */
@Serializable
internal data class EventsFile(
    val version: Int,
    val events: List<EventEntry>,
)

@Serializable
internal data class EventEntry(
    val date: String,
    val category: String,
    val name: String,
    val note: String = "",
)

internal const val SUPPORTED_VERSION = 1

internal fun EventsFile.toDomain(): List<ReminderEvent> {
    require(version == SUPPORTED_VERSION) {
        "events.json is version $version; this build understands $SUPPORTED_VERSION"
    }
    return events.map { it.toDomain() }
}

/**
 * A bad row fails the whole file rather than being skipped: an event silently
 * dropped from your day is exactly the failure this app exists to prevent.
 */
internal fun EventEntry.toDomain(): ReminderEvent {
    val parsed = parseEventDate(date)
    val known = EventCategory.entries.firstOrNull { it.name == category }
        ?: throw IllegalArgumentException("""unknown category "$category" for "$name"""")
    return ReminderEvent(
        name = name,
        month = parsed.month,
        day = parsed.day,
        category = known,
        originalYear = parsed.year,
        note = note,
    )
}
