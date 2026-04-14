package ru.yandex.buggyweatherapp.domain.model

sealed interface WeatherError {
    class RequestError(val message: String) : WeatherError
    class ResponseError(val message: String) : WeatherError
}

sealed interface WeatherResult {
    class Data(
        val weatherData: WeatherData,
        val locationData: Location,
        val cityName: String
    ) : WeatherResult
    class Error(val message: String) : WeatherResult
    //class Error(val error: WeatherError) : WeatherResult
}