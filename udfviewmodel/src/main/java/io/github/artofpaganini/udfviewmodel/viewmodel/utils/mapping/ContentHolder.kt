package io.github.artofpaganini.udfviewmodel.viewmodel.utils.mapping

import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslContentConsumer
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslContentHolder
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslContentSource
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslUiMapper
import androidx.compose.runtime.State as ComposeState

@DslContentHolder
fun <State : Any?, UiState : Any?> contentHolder(
    initial: State,
    mapper: State.() -> UiState
): Lazy<ContentHolder<State, UiState>> = lazy { ContentHolderImpl.create(initial = initial, mapper = mapper) }

@DslContentSource
fun <State : Any?> contentSource(
    initial: State,
): Lazy<ContentSource<State>> = lazy { ContentHolderImpl.create(initial = initial) }

@DslContentConsumer
fun <State : Any?, UiState : Any?> contentConsumer(
    producer: ComposeState<State>,
    mapper: State.() -> UiState
): Lazy<ContentConsumer<UiState>> = lazy { ContentHolderImpl.create(producer = producer, mapper = mapper) }

@DslContentHolder
interface ContentHolder<State : Any?, UiState : Any?> :
    ContentSource<State>,
    ContentConsumer<UiState> {

    override val state: ComposeState<State>

    override val value: State

    override val uiState: ComposeState<UiState>

    override val uiValue: UiState

    @DslContentHolder
    override infix fun updateTo(newValue: @DslUiMapper State)

    @DslContentHolder
    override infix fun updateTo(block: @DslUiMapper State.() -> State)
}

@DslContentSource
interface ContentSource<State : Any?> {

    val state: ComposeState<State>

    val value: State

    @DslContentSource
    infix fun updateTo(newValue: @DslUiMapper State)

    @DslContentSource
    infix fun updateTo(block: @DslUiMapper State.() -> State)
}

@DslContentConsumer
interface ContentConsumer<UiState : Any?> {
    val uiState: ComposeState<UiState>

    val uiValue: UiState
}