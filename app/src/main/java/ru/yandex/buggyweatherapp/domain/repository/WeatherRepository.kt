package ru.yandex.buggyweatherapp.domain.repository

import ru.yandex.buggyweatherapp.domain.model.Location
import ru.yandex.buggyweatherapp.domain.model.WeatherResult

interface WeatherRepository {

    suspend fun getWeatherData(location: Location): WeatherResult

    suspend fun getWeatherByCity(cityName: String): WeatherResult
}