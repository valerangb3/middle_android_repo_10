package ru.yandex.buggyweatherapp.data.repository

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.yandex.buggyweatherapp.R
import ru.yandex.buggyweatherapp.domain.model.LocationResult as LocationResultModel
import ru.yandex.buggyweatherapp.domain.repository.LocationRepository
import ru.yandex.buggyweatherapp.domain.model.Location
import ru.yandex.buggyweatherapp.utils.LocationTracker
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocationRepository {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentLocation(): LocationResultModel {
        return withContext(dispatcher) {
            try {
                var location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    LocationResultModel.Data(Location(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ))
                } else {
                    location = fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).await()
                    LocationResultModel.Data(Location(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ))
                }
            } catch (e: SecurityException) {
                Log.e("LocationRepository", context.getString(R.string.no_location_permission), e)
                LocationResultModel.Error(context.getString(R.string.no_location_permission))
            }
        }
    }

    override fun getCityNameFromLocation(location: Location): String? {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            
            return if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                if (address.locality != null) {
                    address.locality
                } else if (address.subAdminArea != null) {
                    address.subAdminArea
                } else {
                    address.adminArea
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "Error getting city name", e)
            return null
        }
    }

    override fun startLocationTracking() {
        LocationTracker.getInstance(context).startTracking()
    }
}