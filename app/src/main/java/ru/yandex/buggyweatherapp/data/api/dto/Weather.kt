package ru.yandex.buggyweatherapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)