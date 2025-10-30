package io.github.artofpaganini.udfviewmodel.viewmodel.factory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

class SavedStateViewModelFactory<out VM : ViewModel>(
    private val viewModelFactory: SavedStateViewModelAssistedFactory<VM>,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()
        return viewModelFactory.create(handle) as T
    }
}

interface SavedStateViewModelAssistedFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}
