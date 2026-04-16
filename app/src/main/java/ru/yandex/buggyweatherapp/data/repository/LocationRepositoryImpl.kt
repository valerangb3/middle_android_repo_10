package ru.yandex.buggyweatherapp.data.repository

import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    
    
    private var currentLocation: Location? = null
    
    
    private lateinit var locationCallback: LocationCallback

    override suspend fun getCurrentLocation(): LocationResultModel {
        return withContext(dispatcher) {
            try {
                //locationCallback = callback
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
                    //requestLocationUpdates(callback)
                }
            } catch (e: SecurityException) {
                Log.e("LocationRepository", "Location permission not granted", e)
                LocationResultModel.Error("Location permission not granted")
            }
        }
    }


    private suspend fun requestLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val userLocation = Location(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        currentLocation = userLocation
                        //callback(userLocation)
                        stopLocationUpdates()
                    }
                }
            }
            
            val result = fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).await()
        } catch (e: SecurityException) {
            Log.e("LocationRepository", "Location permission not granted", e)
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

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    override fun startLocationTracking() {
        LocationTracker.getInstance(context).startTracking()
    }
    
}