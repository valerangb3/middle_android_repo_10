package ru.yandex.buggyweatherapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
class Coordinate(
    val lon: Double,
    val lat: Double
)
