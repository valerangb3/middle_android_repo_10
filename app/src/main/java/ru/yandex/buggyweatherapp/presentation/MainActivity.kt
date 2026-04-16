package ru.yandex.buggyweatherapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import ru.yandex.buggyweatherapp.WeatherApplication
import ru.yandex.buggyweatherapp.domain.repository.LocationRepository
import ru.yandex.buggyweatherapp.domain.repository.WeatherRepository
import ru.yandex.buggyweatherapp.presentation.ui.screens.WeatherScreen
import ru.yandex.buggyweatherapp.presentation.ui.theme.BuggyWeatherAppTheme
import ru.yandex.buggyweatherapp.presentation.viewmodel.WeatherViewModel
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var locationRepository: LocationRepository
    @Inject
    lateinit var weatherRepository: WeatherRepository

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                
            }
            else -> {
                Toast.makeText(
                    this,
                    "Для работы приложения необходимо разрешение на местоположение ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkLocationPermissionRequest() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO мб проверку перекинуть в compose
        checkLocationPermissionRequest()

        (applicationContext as WeatherApplication).appComponent.inject(this)
        
        enableEdgeToEdge()
        val weatherViewModel = WeatherViewModel(
            weatherRepository = weatherRepository,
            locationRepository = locationRepository
        )
        setContent {
            BuggyWeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        viewModel = weatherViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    
    
    override fun onDestroy() {
        super.onDestroy()
        
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherAppPreview() {
    BuggyWeatherAppTheme {
        
        Text("Weather App Preview")
    }
}