package ru.yandex.buggyweatherapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.yandex.buggyweatherapp.presentation.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [WeatherModule::class])
interface WeatherComponent {

    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(appContext: Context): Builder
        fun build(): WeatherComponent
    }
}