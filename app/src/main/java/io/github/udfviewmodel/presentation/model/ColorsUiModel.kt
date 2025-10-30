package io.github.udfviewmodel.presentation.model

data class ColorsUiModel(
    override val id: String,
    val colors: List<String>
) : UiModel

