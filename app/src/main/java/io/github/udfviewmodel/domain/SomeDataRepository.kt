package io.github.udfviewmodel.domain

import io.github.udfviewmodel.domain.model.AnimalsModel
import io.github.udfviewmodel.domain.model.CarsModel
import io.github.udfviewmodel.domain.model.CitiesModel
import io.github.udfviewmodel.domain.model.ColorsModel
import io.github.udfviewmodel.domain.model.NamesModel
import io.github.udfviewmodel.domain.model.NumbersModel
import kotlinx.coroutines.flow.Flow

interface SomeDataRepository {
    fun getCities(): Flow<CitiesModel>
    fun getCars(): Flow<CarsModel>
    fun getAnimals(): Flow<AnimalsModel>
    fun getNames(): Flow<NamesModel>
    fun getNumbers(): Flow<NumbersModel>
    fun getColors(): Flow<ColorsModel>
}