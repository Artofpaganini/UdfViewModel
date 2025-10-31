package io.github.artofpaganini.udfviewmodel.viewmodel.delegate

import io.github.artofpaganini.udfviewmodel.viewmodel.delegate.provider.UdfDelegateActionProvider
import io.github.artofpaganini.udfviewmodel.viewmodel.delegate.provider.UdfDelegateEventProvider
import io.github.artofpaganini.udfviewmodel.viewmodel.delegate.provider.UdfDelegateStateProvider
import kotlinx.coroutines.CoroutineScope

@JvmSuppressWildcards
interface UdfDelegate<DelegateAction, DelegateState, DelegateEvent>
    : UdfDelegateActionProvider<DelegateAction>,
    UdfDelegateStateProvider<DelegateState>,
    UdfDelegateEventProvider<DelegateEvent>