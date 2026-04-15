package ru.yandex.buggyweatherapp.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.yandex.buggyweatherapp.R
import ru.yandex.buggyweatherapp.presentation.model.Content
import ru.yandex.buggyweatherapp.presentation.model.Error
import ru.yandex.buggyweatherapp.presentation.model.Idle
import ru.yandex.buggyweatherapp.presentation.model.Loading
import ru.yandex.buggyweatherapp.presentation.ui.components.Loading
import ru.yandex.buggyweatherapp.presentation.ui.components.WeatherCard
import ru.yandex.buggyweatherapp.presentation.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    val weatherState by viewModel.weatherState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text(stringResource(R.string.search_city)) },
            placeholder = { Text(stringResource(R.string.search_city)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    if (searchText.isNotBlank()) {
                        focusManager.clearFocus()
                        viewModel.searchWeatherByCity(searchText)
                    }
                }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                }
            },
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchText.isNotBlank()) {
                        focusManager.clearFocus()
                        viewModel.searchWeatherByCity(searchText)
                    }
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        when (weatherState) {
            is Idle -> {}
            is Error -> {
                Text(
                    text = (weatherState as Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            is Loading -> Loading()
            is Content -> {
                WeatherCard(
                    weather = (weatherState as Content).weather,
                    cityName = (weatherState as Content).cityName,
                    onFavoriteClick = { viewModel.toggleFavorite() },
                    onRefreshClick = { viewModel.fetchCurrentLocationWeather() }
                )
            }
        }
    }
}