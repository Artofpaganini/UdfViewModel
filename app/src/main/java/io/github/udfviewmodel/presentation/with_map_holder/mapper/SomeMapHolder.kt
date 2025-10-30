package io.github.udfviewmodel.presentation.with_map_holder.mapper

import io.github.artofpaganini.udfviewmodel.viewmodel.utils.mapping.UiMapper
import io.github.artofpaganini.udfviewmodel.viewmodel.utils.mapping.contentHolder
import io.github.udfviewmodel.domain.model.AnimalsModel
import io.github.udfviewmodel.domain.model.CarsModel
import io.github.udfviewmodel.domain.model.CitiesModel
import io.github.udfviewmodel.domain.model.ColorsModel
import io.github.udfviewmodel.domain.model.NamesModel
import io.github.udfviewmodel.domain.model.NumbersModel
import io.github.udfviewmodel.presentation.model.AnimalsUiModel
import io.github.udfviewmodel.presentation.model.CarsUiModel
import io.github.udfviewmodel.presentation.model.CitiesUiModel
import io.github.udfviewmodel.presentation.model.ColorsUiModel
import io.github.udfviewmodel.presentation.model.NamesUiModel
import io.github.udfviewmodel.presentation.model.NumbersUiModel
import io.github.udfviewmodel.presentation.model.SomeState
import io.github.udfviewmodel.presentation.model.SomeUiState

class SomeMapHolder(
    initialState: SomeState
) : UiMapper<SomeState, SomeUiState> {

    val cities by contentHolder(initialState.cities) {
        mapCities()
    }
    val cars by contentHolder(initialState.cars) {
        mapCars()
    }
    val colors by contentHolder(initialState.colors) {
        mapColors()
    }
    val animals by contentHolder(initialState.animals) {
        mapAnimals()
    }
    val names by contentHolder(initialState.names) {
        mapNames()
    }
    val numbers by contentHolder(initialState.numbers) {
        mapNumbers()
    }

    override fun invoke(state: SomeState): SomeUiState {
        cities updateTo state.cities
        cars updateTo state.cars
        colors updateTo state.colors
        animals updateTo state.animals
        names updateTo state.names
        numbers updateTo state.numbers
        return when {
            state.isLoading -> SomeUiState.Loading
            state.isError -> SomeUiState.Error("State is ERROR")
            else -> {
                SomeUiState.Content(
                    list = listOf(
                        cities.uiValue,
                        names.uiValue,
                        colors.uiValue,
                        animals.uiValue,
                        cars.uiValue,
                        numbers.uiValue
                    )
                )
            }
        }
    }

    private fun CitiesModel.mapCities() = CitiesUiModel(
        id = id,
        cities = data
    )

    private fun CarsModel.mapCars() = CarsUiModel(
        id = id,
        cars = data
    )

    private fun ColorsModel.mapColors() = ColorsUiModel(
        id = id,
        colors = data
    )

    private fun AnimalsModel.mapAnimals() = AnimalsUiModel(
        id = id,
        animals = data
    )

    private fun NamesModel.mapNames() = NamesUiModel(
        id = id,
        names = data
    )

    private fun NumbersModel.mapNumbers() = NumbersUiModel(
        id = id,
        numbers = data
    )
}