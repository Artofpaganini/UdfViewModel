package io.github.artofpaganini.udfviewmodel.viewmodel.state_wrapper

import androidx.annotation.AnyThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@AnyThread
internal class StateStreamWrapper<State>(initialState: State) {

    private val stream = MutableStateFlow<State>(initialState)

    fun getStream(): StateFlow<State> = stream.asStateFlow()

    infix fun updateStateBy(block: State.() -> State) {
        stream.update(block)
    }
}