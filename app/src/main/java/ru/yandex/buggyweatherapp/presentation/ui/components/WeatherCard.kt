package ru.yandex.buggyweatherapp.presentation.ui.components

import android.widget.ImageView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.yandex.buggyweatherapp.R
import ru.yandex.buggyweatherapp.domain.model.WeatherData
import ru.yandex.buggyweatherapp.utils.ImageLoader
import ru.yandex.buggyweatherapp.utils.WeatherIconMapper

@Composable
fun DetailedWeatherCard(weather: WeatherData) {
    val context = LocalContext.current
    
    
    val imageView = remember { ImageView(context) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weather.cityName,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                IconButton(onClick = { /* No-op, should use ViewModel */ }) {
                    Icon(
                        imageVector = if (weather.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite"
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                
                AndroidView(
                    factory = { imageView },
                    modifier = Modifier.size(50.dp)
                ) {
                    
                    val iconUrl = "https://openweathermap.org/img/wn/${weather.icon}@2x.png"
                    ImageLoader.loadInto(iconUrl, it)
                }
                
                
                Text(
                    text = weather.temperature.toString() + "°C",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            
            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            
            LazyColumn {
                item {
                    WeatherDataRow("Feels like", weather.feelsLike.toString() + "°C")
                }
                item {
                    WeatherDataRow("Min/Max", "${weather.minTemp}°C / ${weather.maxTemp}°C")
                }
                item {
                    WeatherDataRow("Humidity", weather.humidity.toString() + "%")
                }
                item {
                    WeatherDataRow("Pressure", weather.pressure.toString() + " hPa")
                }
                item {
                    WeatherDataRow("Wind", weather.windSpeed.toString() + " m/s")
                }
                item {
                    WeatherDataRow("Sunrise", WeatherIconMapper.formatTimestamp(weather.sunriseTime))
                }
                item {
                    WeatherDataRow("Sunset", WeatherIconMapper.formatTimestamp(weather.sunsetTime))
                }
            }
        }
    }
    
    
    DisposableEffect(weather.icon) {
        val iconUrl = "https://openweathermap.org/img/wn/${weather.icon}@2x.png"
        
        
        val bitmap = ImageLoader.loadImageSync(iconUrl)
        imageView.setImageBitmap(bitmap)
        
        onDispose {
            
        }
    }
}

@Composable
fun WeatherCard(
    weather: WeatherData,
    cityName: String,
    onFavoriteClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cityName.ifEmpty { weather.cityName },
                    style = MaterialTheme.typography.headlineMedium
                )

                Row {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (weather.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorite)
                        )
                    }

                    IconButton(onClick = onRefreshClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(WeatherIconMapper.getIconUrl(weather.icon))
                        .crossfade(true)
                        .build(),
                    contentDescription = weather.description,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = stringResource(R.string.temperature, weather.temperature.toString()),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                text = stringResource(R.string.feels_like, weather.feelsLike.toString()),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.description, weather.description.replaceFirstChar { it.uppercase() }),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.humidity, weather.humidity.toString()),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.wind, weather.windSpeed.toString()),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            WeatherDataRow(
                stringResource(R.string.sunrise, WeatherIconMapper.formatTimestamp(weather.sunriseTime)),
                stringResource(R.string.sunset, WeatherIconMapper.formatTimestamp(weather.sunsetTime))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRefreshClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.refresh_weather))
            }
        }
    }
}

@Composable
private fun WeatherDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}