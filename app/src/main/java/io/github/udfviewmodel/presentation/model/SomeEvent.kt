package io.github.udfviewmodel.presentation.model

sealed interface SomeEvent {
    @JvmInline
    value class ShowErrorToast(val message: String) : SomeEvent
}