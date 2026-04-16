package ru.yandex.buggyweatherapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import ru.yandex.buggyweatherapp.WeatherApplication
import ru.yandex.buggyweatherapp.presentation.ui.screens.WeatherScreen
import ru.yandex.buggyweatherapp.presentation.ui.theme.BuggyWeatherAppTheme
import ru.yandex.buggyweatherapp.presentation.viewmodel.WeatherViewModel
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val weatherViewModel by viewModels<WeatherViewModel> { viewModelFactory }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as WeatherApplication).appComponent.inject(this)
        
        enableEdgeToEdge()
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
}