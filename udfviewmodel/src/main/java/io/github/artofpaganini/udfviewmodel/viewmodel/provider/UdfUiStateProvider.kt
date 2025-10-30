package io.github.artofpaganini.udfviewmodel.viewmodel.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import io.github.artofpaganini.udfviewmodel.viewmodel.annotation.DslState
import kotlinx.coroutines.flow.Flow

@DslState
interface UdfUiStateProvider<UiState> {

    @NonRestartableComposable
    @Composable
    fun collectUiState(): State<UiState>

    fun getUiState(): Flow<UiState>
}