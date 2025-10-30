package io.github.artofpaganini.udfviewmodel.viewmodel.delegate.provider

import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslDelegateEvent
import kotlinx.coroutines.flow.Flow

@DslDelegateEvent
interface UdfDelegateEventProvider<DelegateSideEffect> {
    fun getDelegateEvent(): Flow<DelegateSideEffect>
}