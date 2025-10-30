package io.github.artofpaganini.udfviewmodel.viewmodel.delegate.provider

interface UdfDelegateActionProvider<DelegateAction> {

    suspend fun onAction(action: DelegateAction)
}