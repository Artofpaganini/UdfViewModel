package io.github.artofpaganini.udfviewmodel.viewmodel.provider

import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslEvent
import kotlinx.coroutines.flow.Flow

@DslEvent
interface UdfEventProvider<Event> {
    fun getEvent(): Flow<Event>
}