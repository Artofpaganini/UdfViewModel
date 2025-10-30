package io.github.udfviewmodel.presentation.model

import io.github.udfviewmodel.domain.model.AnimalsModel
import io.github.udfviewmodel.domain.model.CarsModel
import io.github.udfviewmodel.domain.model.CitiesModel
import io.github.udfviewmodel.domain.model.ColorsModel
import io.github.udfviewmodel.domain.model.NamesModel
import io.github.udfviewmodel.domain.model.NumbersModel

data class SomeState(
    val isLoading: Boolean,
    val isError: Boolean,
    val cities: CitiesModel,
    val cars: CarsModel,
    val colors: ColorsModel,
    val animals: AnimalsModel,
    val names: NamesModel,
    val numbers: NumbersModel,
) {
    companion object {
        fun empty(): SomeState = SomeState(
            isLoading = true,
            isError = false,
            cities = CitiesModel.empty(),
            cars = CarsModel.empty(),
            colors = ColorsModel.empty(),
            animals = AnimalsModel.empty(),
            names = NamesModel.empty(),
            numbers = NumbersModel.empty()
        )
    }
}
