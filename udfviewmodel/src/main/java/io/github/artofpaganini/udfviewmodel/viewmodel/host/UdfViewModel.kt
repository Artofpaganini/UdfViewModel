package io.github.artofpaganini.udfviewmodel.viewmodel.host

import androidx.lifecycle.ViewModel
import io.github.artofpaganini.udfviewmodel.viewmodel.provider.UdfContentProvider

abstract class UdfViewModel<Action, UiState, SideEffect> : ViewModel(), UdfContentProvider<UiState, SideEffect> {

    abstract fun onAction(action: Action)

    abstract fun onActions(vararg actions: Action)
}