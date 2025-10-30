package io.github.udfviewmodel.presentation.model

sealed interface SomeUiState {

    data object Loading : SomeUiState

    data class Content(
        val list: List<UiModel>
    ) : SomeUiState

    data class Error(
        val message: String
    ) : SomeUiState
}
