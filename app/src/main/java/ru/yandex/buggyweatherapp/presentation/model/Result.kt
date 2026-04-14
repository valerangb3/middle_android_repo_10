package ru.yandex.buggyweatherapp.presentation.model

import ru.yandex.buggyweatherapp.domain.model.Location
import ru.yandex.buggyweatherapp.domain.model.WeatherData

sealed interface Result
object Loading : Result
class Error(val message: String): Result
object Idle : Result
data class Content(
    val weather: WeatherData,
    val currentLocation: Location,
    val cityName: String,
): Result