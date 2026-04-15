package ru.yandex.buggyweatherapp.domain.repository

import ru.yandex.buggyweatherapp.domain.model.Location
import ru.yandex.buggyweatherapp.domain.model.LocationResult

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult
    fun getCityNameFromLocation(location: Location): String?

    //TODO есть подозрение, что данная функция нигде не вызывается
    fun startLocationTracking()
}