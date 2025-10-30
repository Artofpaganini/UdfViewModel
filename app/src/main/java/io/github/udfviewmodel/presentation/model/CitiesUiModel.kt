package io.github.udfviewmodel.presentation.model

data class CitiesUiModel(
    override val id: String,
    val cities: List<String>
) : UiModel