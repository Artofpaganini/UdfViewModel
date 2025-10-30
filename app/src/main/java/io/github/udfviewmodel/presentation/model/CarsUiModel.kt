package io.github.udfviewmodel.presentation.model

data class CarsUiModel(
    override val id: String,
    val cars: List<String>
) : UiModel

