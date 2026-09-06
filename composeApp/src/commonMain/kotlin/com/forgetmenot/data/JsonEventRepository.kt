package com.forgetmenot.data

import com.forgetmenot.domain.ReminderEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * Reads events from a bundled JSON file.
 *
 * [readBytes] is injected rather than called directly, so this class knows
 * nothing about Compose Resources: it can be tested without a running app, and
 * a future source that reads a file the user edits needs no change here.
 */
class JsonEventRepository(
    private val readBytes: suspend (String) -> ByteArray,
    private val path: String = DEFAULT_PATH,
) : EventRepository {

    override fun events(): Flow<List<ReminderEvent>> = flow {
        emit(decodeEvents(readBytes(path).decodeToString()))
    }

    companion object {
        const val DEFAULT_PATH: String = "files/events.json"
    }
}

/** Unknown keys are tolerated so a newer file stays readable by an older build. */
private val json = Json { ignoreUnknownKeys = true }

internal fun decodeEvents(text: String): List<ReminderEvent> =
    json.decodeFromString<EventsFile>(text).toDomain()
