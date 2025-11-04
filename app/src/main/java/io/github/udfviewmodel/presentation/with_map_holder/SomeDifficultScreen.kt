package io.github.udfviewmodel.presentation.with_map_holder

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.artofpaganini.udfviewmodel.utils.LifecycleStartStopActions
import io.github.artofpaganini.udfviewmodel.viewmodel.utils.creation.compose.udfViewModel
import io.github.artofpaganini.udfviewmodel.viewmodel.utils.observe.compose.collectEvent
import io.github.udfviewmodel.presentation.model.AnimalsUiModel
import io.github.udfviewmodel.presentation.model.CarsUiModel
import io.github.udfviewmodel.presentation.model.CitiesUiModel
import io.github.udfviewmodel.presentation.model.ColorsUiModel
import io.github.udfviewmodel.presentation.model.NamesUiModel
import io.github.udfviewmodel.presentation.model.NumbersUiModel
import io.github.udfviewmodel.presentation.model.SomeAction
import io.github.udfviewmodel.presentation.model.SomeEvent
import io.github.udfviewmodel.presentation.model.SomeUiState
import io.github.udfviewmodel.presentation.model.UiModel

@Composable
fun SomeDifficultScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel = udfViewModel(viewModelKey = SomeViewModelWithMapHolder::class, viewModelFactory = null)
    val uiState by viewModel.collectUiState()

    viewModel.collectEvent { event ->
        when (event) {
            is SomeEvent.ShowErrorToast -> Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
        }
        viewModel.onAction(SomeAction.Internal.HandleEvent)
    }
    LifecycleStartStopActions(
        launchAction = {
            viewModel.onAction(SomeAction.Internal.Launch)
        },
        cancelAction = {
            viewModel.onAction(SomeAction.Internal.Cancel)
        }
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (val model = uiState) {
            is SomeUiState.Content -> {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 32.dp),
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = model.list,
                        key = UiModel::id,
                        contentType = { it::class }) {
                        when (it) {
                            is AnimalsUiModel -> {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(it.animals) { text ->
                                        Box(modifier = Modifier.height(50.dp), contentAlignment = Center) {
                                            Text(text = text)
                                        }
                                    }
                                }
                            }
                            is CarsUiModel -> {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(it.cars) { text ->
                                        Box(modifier = Modifier.height(50.dp), contentAlignment = Center) {
                                            Text(text = text)
                                        }
                                    }
                                }
                            }
                            is CitiesUiModel -> {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(it.cities) { text ->
                                        Box(modifier = Modifier.height(50.dp), contentAlignment = Center) {
                                            Text(text = text)
                                        }
                                    }
                                }
                            }
                            is ColorsUiModel -> {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(it.colors) { text ->
                                        Box(modifier = Modifier.height(50.dp), contentAlignment = Center) {
                                            Text(text = text)
                                        }
                                    }
                                }
                            }
                            is NamesUiModel -> {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(it.names) { text ->
                                        Box(modifier = Modifier.height(50.dp), contentAlignment = Center) {
                                            Text(text = text)
                                        }
                                    }
                                }
                            }
                            is NumbersUiModel -> {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(it.numbers) { text ->
                                        Box(modifier = Modifier.height(50.dp), contentAlignment = Center) {
                                            Text(text = text)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is SomeUiState.Error -> Text(text = model.message)
            SomeUiState.Loading -> CircularProgressIndicator()
        }

        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            Button(
                onClick = {
                    viewModel.onActions(SomeAction.Internal.Cancel, SomeAction.Internal.Launch)
                }) {
                Text(text = "Click for RecallData")
            }

            Button(
                onClick = {
                    viewModel.onAction(SomeAction.Ui.CallErrorMessage)
                }) {
                Text(text = "Click for Error")
            }
            var status by remember { mutableStateOf("Нажми кнопку для теста ANR") }

            Box(
                modifier = Modifier.height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = status)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        status = "Главный поток зависает..."
                        viewModel.onAction(SomeAction.Ui.DoAnr)
                        status = "Главный поток снова жив"
                    }) {
                        Text("Сделать ANR")
                    }
                }
            }
        }

    }
}
