package ru.yandex.buggyweatherapp.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import ru.yandex.buggyweatherapp.data.api.WeatherApiService
import ru.yandex.buggyweatherapp.data.repository.LocationRepositoryImpl
import ru.yandex.buggyweatherapp.data.repository.WeatherRepositoryImpl
import ru.yandex.buggyweatherapp.domain.repository.LocationRepository
import ru.yandex.buggyweatherapp.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
class WeatherModule {
    @Singleton
    @Provides
    fun provideLocationRepository(
        appContext: Context,
        dispatcher: CoroutineDispatcher
    ): LocationRepository {
        return LocationRepositoryImpl(
            appContext,
            dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideWeatherRepository(
        weatherApi: WeatherApiService,
        dispatcher: CoroutineDispatcher
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            weatherApi,
            dispatcher
        )
    }

    @Singleton
    @Provides
    fun provideRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WeatherApiService.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}