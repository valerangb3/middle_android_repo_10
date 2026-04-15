package ru.yandex.buggyweatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WeatherIconMapper {
    
    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
    
    
    fun getWeatherDescription(description: String, temperature: Double): String = buildString {
        append(description.replaceFirstChar { it.uppercase() })
        append(", ")
        append("${temperature.toInt()}°C")
    }

    fun getIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/$iconCode@2x.png"
    }
}