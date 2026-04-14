package ru.yandex.buggyweatherapp.data.repository

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.yandex.buggyweatherapp.data.api.WeatherApiService
import ru.yandex.buggyweatherapp.data.api.dto.WeatherResponse
import ru.yandex.buggyweatherapp.data.api.dto.mapToLocationData
import ru.yandex.buggyweatherapp.data.api.dto.mapToWeatherData
import ru.yandex.buggyweatherapp.domain.repository.WeatherRepository
import ru.yandex.buggyweatherapp.domain.model.Location
import ru.yandex.buggyweatherapp.domain.model.WeatherResult

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : WeatherRepository {

    private suspend fun getData(getWeatherData: suspend () -> WeatherResponse): WeatherResult {
        return withContext(dispatcher) {
            try {
                val weatherResponse = getWeatherData()
                val weatherData = weatherResponse.mapToWeatherData()
                val location = weatherResponse.mapToLocationData()
                if (weatherResponse.code == 200) {
                    WeatherResult.Data(
                        locationData = location,
                        cityName = weatherData.cityName,
                        weatherData = weatherData,
                    )
                } else {
                    WeatherResult.Error(
                        message = "Response code (${weatherResponse.code}): request error"
                    )
                }
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                Log.e("WeatherRepository", "Error fetching weather", ex)
                WeatherResult.Error(
                    message = "Weather request error"
                )
            }
        }
    }

    override suspend fun getWeatherData(location: Location): WeatherResult {
        return getData {
            weatherApi.getCurrentWeather(location.latitude, location.longitude)
        }
    }

    override suspend fun getWeatherByCity(cityName: String): WeatherResult {
        return getData {
            weatherApi.getWeatherByCity(cityName)
        }
    }
}