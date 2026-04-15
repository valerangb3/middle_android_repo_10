package ru.yandex.buggyweatherapp.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
class Sys(
    val id: Long,
    val type: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)