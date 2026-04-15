package ru.yandex.buggyweatherapp.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
object RetrofitInstance {
    private val json = Json {
        ignoreUnknownKeys = true // Игнорировать поля, которых нет в коде
        coerceInputValues = true // Если тип пришел неверный, попробовать привести к дефолту
    }
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WeatherApiService.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    
    val weatherApi: WeatherApiService = retrofit.create(WeatherApiService::class.java)
}