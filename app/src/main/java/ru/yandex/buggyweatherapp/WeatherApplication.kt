package ru.yandex.buggyweatherapp

import android.app.Application
import android.content.Context
import ru.yandex.buggyweatherapp.di.DaggerWeatherComponent
import ru.yandex.buggyweatherapp.di.WeatherComponent
import ru.yandex.buggyweatherapp.utils.ImageLoader
import ru.yandex.buggyweatherapp.utils.LocationTracker

class WeatherApplication : Application() {
    

    lateinit var appComponent: WeatherComponent

    companion object {
        lateinit var appContext: Context
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        
        
        appContext = this
        appComponent = DaggerWeatherComponent.builder()
            .context(appContext = appContext)
            .build()
        
        ImageLoader.initialize(this)
        LocationTracker.getInstance(this)
    }
}