package io.github.udfviewmodel.presentation.with_map_holder

import io.github.artofpaganini.udfviewmodel.viewmodel.UdfBaseViewModel
import io.github.artofpaganini.udfviewmodel.viewmodel.utils.dispatchers.udfDispatchers
import io.github.udfviewmodel.data.SomeDataRepositoryImpl
import io.github.udfviewmodel.domain.SomeDataRepository
import io.github.udfviewmodel.presentation.model.SomeAction
import io.github.udfviewmodel.presentation.model.SomeEvent
import io.github.udfviewmodel.presentation.model.SomeEvent.*
import io.github.udfviewmodel.presentation.model.SomeState
import io.github.udfviewmodel.presentation.model.SomeUiState
import io.github.udfviewmodel.presentation.with_map_holder.mapper.SomeMapHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach

class SomeViewModelWithMapHolder(
    private val repository: SomeDataRepository = SomeDataRepositoryImpl() //I'm too lazy for DI-setup
) : UdfBaseViewModel<SomeAction, SomeUiState, SomeState, SomeEvent>(
    initialState = {
        SomeState.Companion.empty()
    },
    dispatchers = udfDispatchers(
        map = Dispatchers.Main,
        work = Dispatchers.Default
    ),
    mapHolder = ::SomeMapHolder
) {

    private var citiesJob: Job? = null
    private var carsJob: Job? = null
    private var colorsJob: Job? = null
    private var animalsJob: Job? = null
    private var numbersJob: Job? = null
    private var namesJob: Job? = null

    override fun onAction(action: SomeAction) {
        super.onAction(action)
        when (action) {
            SomeAction.Internal.HandleEvent -> handleEvent()
            SomeAction.Internal.Launch -> launchStreams()
            SomeAction.Internal.Cancel -> cancelStreams()
            SomeAction.Ui.CallErrorMessage -> {
                updateState { copy(isError = true) }
                postEvent(ShowErrorToast("Error toast message"))
            }
            SomeAction.Ui.DoAnr ->  Thread.sleep(10_000)
        }
    }

    private fun launchStreams() {
        launch {
            updateState { copy(isLoading = true, isError = false) }
            delay(3000)
            updateState { copy(isLoading = false) }
            loadCities()
            loadNames()
            loadColors()
            loadAnimals()
            loadCars()
            loadNumbers()
        }
    }

    private fun cancelStreams() {
        citiesJob?.cancel()
        carsJob?.cancel()
        colorsJob?.cancel()
        animalsJob?.cancel()
        numbersJob?.cancel()
        namesJob?.cancel()
    }

    private fun loadCities() {
        if (citiesJob?.isActive == true || state.isError) return
        citiesJob = repository.getCities()
            .onEach { cities ->
                updateState { copy(cities = cities) }
            }.launchInScope()
    }

    private fun loadCars() {
        if (carsJob?.isActive == true || state.isError) return
        carsJob = repository.getCars()
            .onEach { cars ->
                updateState { copy(cars = cars) }
            }.launchInScope()
    }

    private fun loadColors() {
        if (colorsJob?.isActive == true || state.isError) return
        colorsJob = repository.getColors()
            .onEach { colors ->
                updateState { copy(colors = colors) }
            }.launchInScope()
    }

    private fun loadAnimals() {
        if (animalsJob?.isActive == true || state.isError) return
        animalsJob = repository.getAnimals()
            .onEach { animals ->
                updateState { copy(animals = animals) }
            }.launchInScope()
    }

    private fun loadNumbers() {
        if (numbersJob?.isActive == true || state.isError) return
        numbersJob = repository.getNumbers()
            .onEach { numbers ->
                updateState { copy(numbers = numbers) }
            }.launchInScope()
    }

    private fun loadNames() {
        if (namesJob?.isActive == true || state.isError) return
        namesJob = repository.getNames()
            .onEach { names ->
                updateState { copy(names = names) }
            }.launchInScope()
    }
}