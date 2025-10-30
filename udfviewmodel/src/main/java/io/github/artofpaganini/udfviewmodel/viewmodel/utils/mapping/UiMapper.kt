package io.github.artofpaganini.udfviewmodel.viewmodel.utils.mapping

import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslState
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslUiMapper

@DslUiMapper
fun interface UiMapper<@DslState State : Any, UiState : Any> {

    fun invoke(state: State): UiState
}