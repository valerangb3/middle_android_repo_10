package ru.yandex.buggyweatherapp.domain.model

sealed interface LocationResult {
    class Data(
        val location: Location
    ) : LocationResult
    class Error(
        val message: String
    ) : LocationResult
}