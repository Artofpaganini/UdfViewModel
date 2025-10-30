package io.github.udfviewmodel.presentation.model

data class NumbersUiModel(
    override val id: String,
    val numbers: List<String>
) : UiModel
