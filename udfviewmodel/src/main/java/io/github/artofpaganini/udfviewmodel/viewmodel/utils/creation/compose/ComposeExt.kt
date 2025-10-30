package io.github.artofpaganini.udfviewmodel.viewmodel.utils.creation.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.github.artofpaganini.udfviewmodel.viewmodel.factory.SavedStateViewModelFactory
import io.github.artofpaganini.udfviewmodel.viewmodel.host.UdfViewModel
import kotlin.reflect.KClass

@Composable
@JvmName("udfViewModel")
fun <Action, UiState, Event> udfViewModel(
    viewModelKey: KClass<out UdfViewModel<Action, UiState, Event>>? = null,
    customKey: String? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    },
    viewModelFactory: ViewModelProvider.Factory?,
): UdfViewModel<Action, UiState, Event> = getViewModel(
    viewModelKey = viewModelKey,
    customKey = customKey,
    viewModelStoreOwner = viewModelStoreOwner,
    viewModelFactory = viewModelFactory,
    extras = extras
)

@Composable
@JvmName("udfSavedStateViewModel")
fun <Action, UiState, Event> udfViewModel(
    viewModelKey: KClass<out UdfViewModel<Action, UiState, Event>>? = null,
    customKey: String? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    },
    savedStateViewModelFactory: SavedStateViewModelFactory<ViewModel>?
): UdfViewModel<Action, UiState, Event> = getViewModel(
    viewModelKey = viewModelKey,
    customKey = customKey,
    viewModelStoreOwner = viewModelStoreOwner,
    viewModelFactory = savedStateViewModelFactory,
    extras = extras
)

@Composable
private inline fun <reified VM : ViewModel> getViewModel(
    viewModelFactory: ViewModelProvider.Factory?,
    viewModelKey: KClass<out VM>? = null,
    customKey: String? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }
): VM {
    val provider = when {
        viewModelFactory != null -> ViewModelProvider.create(
            store = viewModelStoreOwner.viewModelStore,
            factory = viewModelFactory,
            extras = extras
        )
        viewModelStoreOwner is HasDefaultViewModelProviderFactory -> ViewModelProvider.create(
            store = viewModelStoreOwner.viewModelStore,
            factory = viewModelStoreOwner.defaultViewModelProviderFactory,
            extras = extras
        )
        else -> ViewModelProvider.create(owner = viewModelStoreOwner)
    }

    return when {
        customKey != null -> provider[customKey, viewModelKey ?: VM::class]
        viewModelKey != null -> provider[viewModelKey]
        else -> provider[VM::class]
    }
}