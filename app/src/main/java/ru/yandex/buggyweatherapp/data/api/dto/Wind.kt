package ru.yandex.buggyweatherapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)