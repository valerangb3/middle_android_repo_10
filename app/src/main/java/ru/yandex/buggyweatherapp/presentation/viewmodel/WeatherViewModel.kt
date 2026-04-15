package ru.yandex.buggyweatherapp.presentation.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.domain.model.Location
import ru.yandex.buggyweatherapp.domain.model.WeatherData
import ru.yandex.buggyweatherapp.data.repository.LocationRepositoryImpl
import ru.yandex.buggyweatherapp.data.repository.WeatherRepositoryImpl
import ru.yandex.buggyweatherapp.domain.model.LocationResult
import ru.yandex.buggyweatherapp.domain.model.WeatherResult
import ru.yandex.buggyweatherapp.domain.repository.LocationRepository
import ru.yandex.buggyweatherapp.domain.repository.WeatherRepository
import ru.yandex.buggyweatherapp.presentation.model.Content
import ru.yandex.buggyweatherapp.presentation.model.Error
import ru.yandex.buggyweatherapp.presentation.model.Idle
import ru.yandex.buggyweatherapp.presentation.model.Loading
import ru.yandex.buggyweatherapp.presentation.model.Result
import ru.yandex.buggyweatherapp.utils.ImageLoader
import java.util.Timer
import java.util.TimerTask

class WeatherViewModel(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Result>(Idle)
    val weatherState = _state.asStateFlow()

    //TODO зачем зедсь свой scope?
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var refreshJob: Job? = null
    private var weatherJob: Job? = null
    
    init {
        fetchCurrentLocationWeather()
        startAutoRefresh()
    }
    
    fun fetchCurrentLocationWeather() {
        _state.value = Loading
        viewModelScope.launch {
            when (val locationResult = locationRepository.getCurrentLocation()) {
                is LocationResult.Error -> {
                    _state.value = Error(message = "Unable to get current location")
                }
                is LocationResult.Data -> {
                    //TODO - для чего cityNameFromLocation?
                    val cityNameFromLocation = locationRepository
                        .getCityNameFromLocation(locationResult.location)
                    getWeatherForLocation(locationResult.location)
                }
            }
        }
    }
    
    fun getWeatherForLocation(location: Location) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            _state.value = Loading
            handlerWeatherResult(weatherRepository.getWeatherData(location))
        }
    }
    
    fun searchWeatherByCity(city: String) {
        if (city.isBlank()) {
            _state.value = Error(message = "City name cannot be empty")
            return
        }
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            _state.value = Loading
            handlerWeatherResult(weatherRepository.getWeatherByCity(city))
        }
    }

    private fun handlerWeatherResult(weatherResult: WeatherResult) {

        when (weatherResult) {
            is WeatherResult.Data -> {
                _state.value = Content(
                    cityName = weatherResult.cityName,
                    weather = weatherResult.weatherData,
                )
            }
            is WeatherResult.Error -> {
                _state.value = Error(
                    weatherResult.message
                )
            }
        }
    }
    
    
    fun formatTemperature(temp: Double): String {
        return "${temp.toInt()}°C"
    }
    
    
    fun loadWeatherIcon(iconCode: String) {
        coroutineScope.launch {
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
            ImageLoader.loadImage(iconUrl)
        }
    }
    
    
    private fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                Log.d("startAutoRefresh", "startAutoRefresh")
                delay(REFRESH_DELAY)
                val result = weatherState.value
                if (result is Content) {
                    result.currentLocation?.let { location ->
                        getWeatherForLocation(location)
                    }
                }
            }
        }
    }
    
    
    fun toggleFavorite() {
        _state.update { res ->
            if (res is Content) {
                res.copy(
                    weather = res.weather.copy(isFavorite = !res.weather.isFavorite)
                )
            } else res
        }
    }
    
    
    override fun onCleared() {
        super.onCleared()
        weatherJob?.cancel()
        refreshJob?.cancel()
    }

    companion object {
        private const val REFRESH_DELAY = 60000L
    }
}