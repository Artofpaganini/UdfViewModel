package io.github.artofpaganini.udfviewmodel.viewmodel

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import io.github.artofpaganini.udfviewmodel.ActionLogger
import io.github.artofpaganini.udfviewmodel.utils.launch
import io.github.artofpaganini.udfviewmodel.utils.launchIn
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslBaseViewModel
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslEvent
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslState
import io.github.artofpaganini.udfviewmodel.viewmodel.delegate.UdfDelegate
import io.github.artofpaganini.udfviewmodel.viewmodel.delegate.provider.UdfDelegateActionProvider
import io.github.artofpaganini.udfviewmodel.viewmodel.event_wrapper.EventStreamWrapper
import io.github.artofpaganini.udfviewmodel.viewmodel.host.UdfViewModel
import io.github.artofpaganini.udfviewmodel.viewmodel.state_wrapper.StateStreamWrapper
import io.github.artofpaganini.udfviewmodel.viewmodel.utils.dispatchers.UdfDispatchers
import io.github.artofpaganini.udfviewmodel.viewmodel.utils.mapping.UiMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import kotlin.reflect.KProperty
import androidx.compose.runtime.State as ComposeState

typealias OnDelegateEvent<DelegateEvent, Event, DelegateAction> = (
        ((DelegateEvent) -> Event) -> Unit,
        (() -> DelegateAction) -> Unit,
) -> Unit

@DslBaseViewModel
abstract class UdfBaseViewModel<Action : Any, UiState : Any, State : Any, Event : Any>(
    private val initialState: () -> State,
    private val mapper: (state: State) -> UiState,
    private val dispatchers: UdfDispatchers,
) : UdfViewModel<Action, UiState, Event>() {

    constructor(
        initialState: () -> State,
        dispatchers: UdfDispatchers,
        mapHolder: (State) -> UiMapper<State, UiState>
    ) : this(
        initialState = initialState,
        mapper = mapHolder(initialState.invoke())::invoke,
        dispatchers = dispatchers
    )

    private val vmName = this@UdfBaseViewModel::class.java.simpleName

    private val stateWrapper = StateStreamWrapper(initialState.invoke())

    private val eventStreamWrapper by lazy { EventStreamWrapper<Event?>(vmName) }

    private var lastAction: Action? = null

    private var onReturnPredicate: (() -> Boolean)? = null

    val state by stateWrapper.getStream()

    @MainThread
    override fun onAction(action: Action) {
        ActionLogger.log(action::class.java.simpleName)
        vmName.logStage(
            methodName = "onAction",
            message = "Выполнить Action ${action::class.java.simpleName}"
        )
    }

    final override fun onActions(vararg actions: Action) {
        val isPredicate = onReturnPredicate?.invoke()
        when {
            isPredicate == null -> actions.forEach { current -> onAction(current) }
            isPredicate -> {
                val list = lastAction?.let { last -> listOf(last) + actions } ?: actions.toList()
                list.forEach(::onAction)
                onReturnPredicate = null
            }
            !isPredicate -> {
                actions.forEach { current -> onAction(current) }
                onReturnPredicate = null
            }
        }
        lastAction = actions.lastOrNull()
    }

    @MainThread
    final override fun getEvent(): Flow<Event> = eventStreamWrapper.getStream().filterNotNull()

    @NonRestartableComposable
    @Composable
    final override fun collectUiState(): ComposeState<UiState> = remember(this) {
        stateWrapper.getStream()
            .map(mapper::invoke)
            .stateIn(
                scope = viewModelScope + dispatchers.map,
                started = SharingStarted.WhileSubscribed(),
                initialValue = mapper.invoke(initialState.invoke())
            )
    }.collectAsState()

    @AnyThread
    final override fun getUiState(): Flow<UiState> = stateWrapper.getStream()
        .map(mapper::invoke)
        .flowOn(dispatchers.map)

    @DslState
    protected fun updateState(block: @DslState State.() -> State) {
        stateWrapper updateStateBy block
    }

    @DslEvent
    protected fun postEvent(event: @DslEvent Event) {
        eventStreamWrapper.post(event)
    }

    @DslEvent
    protected fun handleEvent() {
        eventStreamWrapper.handle()
    }

    protected fun doLastActionOnReturn(predicate: () -> Boolean) {
        if (onReturnPredicate != predicate) onReturnPredicate = predicate
    }

    /**
     * Не забывайте отменять подписку при уходе с экрана. В частности, в случаях, которые приводят пересозданию активити
     */
    protected fun <DelegateAction, DelegateState, DelegateEvent> UdfDelegate<DelegateAction, DelegateState, DelegateEvent>.observeDelegate(
        coroutineDispatcher: CoroutineDispatcher? = null,
        onState: (DelegateState.() -> Unit)? = null,
        onStateError: (suspend (Throwable) -> Unit)? = null,
        onEvent: OnDelegateEvent<DelegateEvent, Event, DelegateAction>? = null,
        onEventError: (suspend (Throwable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Job = viewModelScope.launch(
        catchBlock = { throwable -> onError?.invoke(throwable) ?: throwable.printStackTrace() },
        context = (coroutineDispatcher ?: dispatchers.work),
    ) {
        if (onState == null && onEvent == null) cancel()
        if (onState != null || onEvent != null)
            onState?.let { stateCallback ->
                getDelegateState()
                    .filterNotNull()
                    .onEach(stateCallback::invoke)
                    .launchIn(
                        catchBlock = { throwable -> onStateError?.invoke(throwable) ?: throwable.printStackTrace() },
                        scope = this
                    )
            }
        onEvent?.let { eventCallback ->
            getDelegateEvent()
                .filterNotNull()
                .onEach { delegateEvent ->
                    eventCallback.invoke(
                        { parentEvent -> postEvent(parentEvent.invoke(delegateEvent)) },
                        { handle -> onDelegateAction(handle.invoke()) }
                    )
                }.launchIn(
                    catchBlock = { throwable -> onEventError?.invoke(throwable) ?: throwable.printStackTrace() },
                    scope = this
                )
        }
    }

    @DslBaseViewModel
    protected fun launch(
        coroutineDispatcher: CoroutineDispatcher? = null,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend () -> Unit
    ): Job = viewModelScope.launch(
        catchBlock = { throwable -> onError?.invoke(throwable) ?: throwable.printStackTrace() },
        context = (coroutineDispatcher ?: dispatchers.work)
    ) {
        block.invoke()
    }

    @DslBaseViewModel
    protected fun <T> UdfDelegateActionProvider<T>.onDelegateAction(
        action: T,
        coroutineDispatcher: CoroutineDispatcher? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Job = viewModelScope.launch(
        catchBlock = { throwable -> onError?.invoke(throwable) ?: throwable.printStackTrace() },
        context = (coroutineDispatcher ?: dispatchers.work)
    ) {
        onAction(action)
    }

    @DslBaseViewModel
    protected fun <T> UdfDelegateActionProvider<T>.onDelegateActions(
        vararg actions: T,
        coroutineDispatcher: CoroutineDispatcher? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Job = viewModelScope.launch(
        catchBlock = { throwable -> onError?.invoke(throwable) ?: throwable.printStackTrace() },
        context = (coroutineDispatcher ?: dispatchers.work)
    ) {
        actions.forEach { action -> onAction(action) }
    }

    protected fun <T> Flow<T>.launchInScope(
        coroutineDispatcher: CoroutineDispatcher? = null,
        catchBlock: suspend (t: Throwable) -> Unit = Throwable::printStackTrace,
    ): Job = catch { throwable -> catchBlock.invoke(throwable) }
        .launchIn(viewModelScope + (coroutineDispatcher ?: dispatchers.work))

    private operator fun <T> StateFlow<T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.value
}

internal fun String.logStage(methodName: String, message: String) {
    println("$this:$methodName $message")
}
