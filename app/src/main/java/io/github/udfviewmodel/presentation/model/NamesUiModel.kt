package io.github.udfviewmodel.presentation.model

data class NamesUiModel(
    override val id: String,
    val names: List<String>
) : UiModel

