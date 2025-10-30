package io.github.artofpaganini.udfviewmodel.viewmodel.provider

interface UdfContentProvider<UiState, SideEffect> :
    UdfUiStateProvider<UiState>,
    UdfEventProvider<SideEffect>
