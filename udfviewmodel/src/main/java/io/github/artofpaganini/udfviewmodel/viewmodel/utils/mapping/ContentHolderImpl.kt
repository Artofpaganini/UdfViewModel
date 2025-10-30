package io.github.artofpaganini.udfviewmodel.viewmodel.utils.mapping

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import kotlin.reflect.KProperty
import androidx.compose.runtime.State as ComposeState

internal class ContentHolderImpl<State : Any?, UiState : Any?> private constructor(
    initial: State,
    private val mapper: State.() -> UiState
) : ContentHolder<State, UiState> {

    constructor(
        producer: ComposeState<State>,
        mapper: State.() -> UiState
    ) : this(
        initial = producer.value,
        mapper = mapper
    ) {
        producerState = producer
    }

    private var producerState: ComposeState<State>? = null

    private val mutableState: MutableState<State> by lazy { mutableStateOf(initial) }

    override val state: ComposeState<State> = mutableState

    override val uiState: ComposeState<UiState> = derivedStateOf {
        producerState?.value?.mapper() ?: mutableState.value.mapper()
    }

    override val value: State by state

    override val uiValue: UiState by uiState

    override infix fun updateTo(block: State.() -> State) {
        mutableState.value = block.invoke(mutableState.value)
    }

    override infix fun updateTo(newValue: State) {
        mutableState.value = newValue
    }

    private operator fun <Content> ComposeState<Content>.getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Content = this.value

    companion object {

        fun <Content : Any?, UiState : Any?> create(
            initial: Content,
            mapper: Content.() -> UiState
        ): ContentHolder<Content, UiState> = ContentHolderImpl(initial = initial, mapper = mapper)

        fun <Content : Any?> create(
            initial: Content
        ): ContentHolder<Content, Content> = ContentHolderImpl(initial = initial) { this }

        fun <Content : Any?, UiState : Any?> create(
            producer: ComposeState<Content>,
            mapper: Content.() -> UiState
        ): ContentHolder<Content, UiState> = ContentHolderImpl(producer = producer, mapper = mapper)
    }
}