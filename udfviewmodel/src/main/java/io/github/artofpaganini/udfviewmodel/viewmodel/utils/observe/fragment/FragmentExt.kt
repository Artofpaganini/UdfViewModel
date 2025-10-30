package io.github.artofpaganini.udfviewmodel.viewmodel.utils.observe.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.github.artofpaganini.udfviewmodel.utils.observe
import io.github.artofpaganini.udfviewmodel.viewmodel.provider.UdfContentProvider

inline fun <reified UiState : Any, reified SideEffect : Any> UdfContentProvider<UiState, SideEffect>.observeContent(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline uiState: ((state: UiState) -> Unit)? = null,
    noinline sideEffect: ((effect: SideEffect) -> Unit)? = null
) {
    uiState?.let {
        getUiState().observe(lifecycleOwner, minActiveState, uiState)
    }
    sideEffect?.let {
        getEvent().observe(lifecycleOwner, Lifecycle.State.STARTED, sideEffect)
    }
}

inline fun <reified UiState : Any, reified SideEffect : Any> UdfContentProvider<UiState, SideEffect>.observeContent(
    fragment: Fragment,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline uiState: ((state: UiState) -> Unit)? = null,
    noinline sideEffect: ((effect: SideEffect) -> Unit)? = null
) {
    uiState?.let {
        getUiState().observe(fragment, minActiveState, uiState)
    }
    sideEffect?.let {
        getEvent().observe(fragment, Lifecycle.State.STARTED, sideEffect)
    }
}