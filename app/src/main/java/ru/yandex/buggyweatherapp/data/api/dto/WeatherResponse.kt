package ru.yandex.buggyweatherapp.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.yandex.buggyweatherapp.domain.model.Location
import ru.yandex.buggyweatherapp.domain.model.WeatherData

@Serializable
data class WeatherResponse(
    val coord: Coordinate,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Long,
    val name: String,
    @SerialName("cod") val code: Int,
)

fun WeatherResponse.mapToWeatherData() = WeatherData(
    cityName = this.name,
    country = this.sys.country,
    temperature = this.main.temp,
    feelsLike = this.main.feelsLike,
    minTemp = this.main.tempMin,
    maxTemp = this.main.tempMax,
    humidity = this.main.humidity,
    pressure = this.main.pressure,
    windSpeed = this.wind.speed,
    windDirection = this.wind.deg,
    description = this.weather[0].description,
    icon = this.weather[0].icon,
    cloudiness = this.clouds.all,
    sunriseTime = this.sys.sunrise,
    sunsetTime = this.sys.sunset,
    timezone =  this.timezone,
    timestamp = this.dt,
)

fun WeatherResponse.mapToLocationData() = Location(
    latitude = this.coord.lat,
    longitude = this.coord.lon,
    name = this.name
)