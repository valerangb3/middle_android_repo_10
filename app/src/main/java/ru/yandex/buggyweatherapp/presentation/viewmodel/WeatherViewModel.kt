package ru.yandex.buggyweatherapp.presentation.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.domain.model.Location
import ru.yandex.buggyweatherapp.domain.model.WeatherData
import ru.yandex.buggyweatherapp.data.repository.LocationRepositoryImpl
import ru.yandex.buggyweatherapp.data.repository.WeatherRepositoryImpl
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

    /*
    private val weatherRepository = WeatherRepositoryImpl()
    private val locationRepository by lazy {
        LocationRepositoryImpl(activityContext)
    }
    */

    private val _state = MutableStateFlow<Result>(Idle)
    val weatherState = _state.asStateFlow()

    val weatherData = MutableLiveData<WeatherData>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val cityName = MutableLiveData<String>()
    

    //TODO зачем зедсь свой scope?
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    
    
    private var refreshTimer: Timer? = null
    
    
    fun initialize(context: Context) {
        fetchCurrentLocationWeather()
        
        //
        startAutoRefresh()
    }
    
    
    fun fetchCurrentLocationWeather() {
        _state.value = Loading
        
        viewModelScope.launch {
            locationRepository.getCurrentLocation { location ->
                location?.let { location ->
                    val cityNameFromLocation = locationRepository.getCityNameFromLocation(location)
                    cityName.value = cityNameFromLocation ?: ""

                    getWeatherForLocation(location)
                    /*_state.update {  }
                    if (weatherState !is Content) {
                        _state.value = Content(

                        )
                    } else {
                        _state.update {

                        }
                    }*/
                } ?: run {
                    _state.value = Error(message = "Unable to get current location")
                }
            }
        }
    }
    
    fun getWeatherForLocation(location: Location) {
        viewModelScope.launch {
            _state.value = Loading
            handlerWeatherResult(weatherRepository.getWeatherData(location))
        }
    }
    
    fun searchWeatherByCity(city: String) {
        if (city.isBlank()) {
            _state.value = Error(message = "City name cannot be empty")
            return
        }
        viewModelScope.launch {
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
        refreshTimer = Timer()
        refreshTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                /*currentLocation.value?.let { location ->
                    getWeatherForLocation(location)
                }*/
            }
        }, 60000, 60000)
    }
    
    
    fun toggleFavorite() {
        weatherData.value?.let {
            it.isFavorite = !it.isFavorite
            
            weatherData.value = it
        }
    }
    
    
    override fun onCleared() {
        super.onCleared()
        
    }
}