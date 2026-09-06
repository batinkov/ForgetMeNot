package com.forgetmenot.data

import com.forgetmenot.domain.ReminderEvent
import kotlinx.coroutines.flow.Flow

/**
 * Where events come from.
 *
 * A Flow rather than a suspend function, so a source that can change — a file
 * being edited, several files merged, a calendar pulled each December — can
 * emit again without this interface or anything above it changing shape.
 */
interface EventRepository {
    fun events(): Flow<List<ReminderEvent>>
}
