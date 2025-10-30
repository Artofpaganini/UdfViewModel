package io.github.artofpaganini.udfviewmodel.viewmodel.delegate

import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslDelegateEvent
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslDelegateState
import io.github.artofpaganini.udfviewmodel.viewmodel.event_wrapper.EventStreamWrapper
import io.github.artofpaganini.udfviewmodel.viewmodel.logStage
import io.github.artofpaganini.udfviewmodel.viewmodel.state_wrapper.StateStreamWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlin.reflect.KProperty

abstract class UdfBaseDelegate<DelegateAction : Any, DelegateState : Any, DelegateEvent : Any>(
    initialState: () -> DelegateState,
) : UdfDelegate<DelegateAction, DelegateState, DelegateEvent> {

    private val vmDelegateTag = this@UdfBaseDelegate::class.java.simpleName

    private val stateStreamWrapper = StateStreamWrapper(initialState.invoke())

    val delegateState by stateStreamWrapper.getStream()

    private val sideEffectWrapper by lazy { EventStreamWrapper<DelegateEvent?>(vmDelegateTag) }

    override suspend fun onAction(action: DelegateAction) {
        vmDelegateTag.logStage(
            methodName = "onAction",
            message = "Выполнить Action ${action::class.java.simpleName}"
        )
    }

    override fun getDelegateState(): Flow<DelegateState> = stateStreamWrapper.getStream()
        .filterNotNull()

    override fun getDelegateEvent(): Flow<DelegateEvent> = sideEffectWrapper.getStream()
        .filterNotNull()

    @DslDelegateState
    protected fun updateDelegateState(block: @DslDelegateState DelegateState.() -> DelegateState) {
        stateStreamWrapper updateStateBy block
    }

    @DslDelegateEvent
    protected fun postDelegateEvent(newEffect: @DslDelegateEvent DelegateEvent) {
        sideEffectWrapper post newEffect
    }

    @DslDelegateEvent
    protected fun postOnReturnDelegateEvent(
        newEffect: @DslDelegateEvent DelegateEvent,
        condition: () -> Boolean
    ) {
        sideEffectWrapper.postOnReturnEvent(newEffect, condition)
    }

    @DslDelegateEvent
    protected fun handleDelegateEvent() {
        sideEffectWrapper.handle()
    }

    private operator fun <T> StateFlow<T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.value
}
