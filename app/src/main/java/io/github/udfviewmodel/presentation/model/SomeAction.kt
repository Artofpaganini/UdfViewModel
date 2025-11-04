package io.github.udfviewmodel.presentation.model

sealed interface SomeAction {
    sealed interface Ui : SomeAction {
        data object CallErrorMessage : Ui
        data object DoAnr : Ui
    }

    sealed interface Internal : SomeAction {
        data object Launch : Internal
        data object Cancel : Internal
        data object HandleEvent : Internal
    }
}