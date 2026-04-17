package ru.yandex.buggyweatherapp.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.yandex.buggyweatherapp.BuildConfig
import ru.yandex.buggyweatherapp.data.api.dto.WeatherResponse

interface WeatherApiService {
    
    
    companion object {
        const val API_KEY = BuildConfig.WEATHER_API_KEY
        const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
    }
    
    
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherResponse
    
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}