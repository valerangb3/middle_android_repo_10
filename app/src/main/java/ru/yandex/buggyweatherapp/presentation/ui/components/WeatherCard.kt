package ru.yandex.buggyweatherapp.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.yandex.buggyweatherapp.R
import ru.yandex.buggyweatherapp.domain.model.WeatherData
import ru.yandex.buggyweatherapp.utils.WeatherIconMapper

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