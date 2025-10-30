package io.github.udfviewmodel.presentation.model

data class AnimalsUiModel(
    override val id: String,
    val animals: List<String>
) : UiModel