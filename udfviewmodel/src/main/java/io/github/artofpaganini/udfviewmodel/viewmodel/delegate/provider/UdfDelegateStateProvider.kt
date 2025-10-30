package io.github.artofpaganini.udfviewmodel.viewmodel.delegate.provider

import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslDelegateState
import kotlinx.coroutines.flow.Flow

@DslDelegateState
interface UdfDelegateStateProvider<DelegateState> {
    fun getDelegateState(): Flow<DelegateState>
}