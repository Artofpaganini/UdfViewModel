package io.github.artofpaganini.udfviewmodel.viewmodel.utils.creation.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import io.github.artofpaganini.udfviewmodel.viewmodel.factory.SavedStateViewModelFactory
import io.github.artofpaganini.udfviewmodel.viewmodel.host.UdfViewModel
import kotlin.reflect.KClass

@JvmName("udfSavedStateViewModel")
fun <Action, UiState, SideEffect> Fragment.udfViewModel(
    viewModelKey: KClass<out UdfViewModel<Action, UiState, SideEffect>>? = null,
    ownerProducer: () -> ViewModelStoreOwner = { this },
    extrasProducer: (() -> CreationExtras)? = null,
    savedStateViewModelFactory: () -> SavedStateViewModelFactory<ViewModel>
): Lazy<UdfViewModel<Action, UiState, SideEffect>> = getLazyViewModel<UdfViewModel<Action, UiState, SideEffect>>(
    viewModelKey = viewModelKey,
    ownerProducer = ownerProducer,
    extrasProducer = extrasProducer,
    factoryProducer = savedStateViewModelFactory
)

@JvmName("udfViewModel")
fun <Action, UiState, SideEffect> Fragment.udfViewModel(
    viewModelKey: KClass<out UdfViewModel<Action, UiState, SideEffect>>? = null,
    ownerProducer: () -> ViewModelStoreOwner = { this },
    extrasProducer: (() -> CreationExtras)? = null,
    viewModelFactory: () -> ViewModelProvider.Factory
): Lazy<UdfViewModel<Action, UiState, SideEffect>> = getLazyViewModel<UdfViewModel<Action, UiState, SideEffect>>(
    viewModelKey = viewModelKey,
    ownerProducer = ownerProducer,
    extrasProducer = extrasProducer,
    factoryProducer = viewModelFactory
)

private inline fun <reified VM : ViewModel> Fragment.getLazyViewModel(
    viewModelKey: KClass<out VM>? = null,
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: () -> ViewModelProvider.Factory
): Lazy<VM> {
    val owner by lazy(LazyThreadSafetyMode.NONE) { ownerProducer() }
    return createViewModelLazy(
        viewModelClass = viewModelKey ?: VM::class,
        storeProducer = { owner.viewModelStore },
        extrasProducer = {
            extrasProducer?.invoke()
                ?: (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
                ?: CreationExtras.Empty
        },
        factoryProducer = factoryProducer
    )
}