package com.example.traveljournal.util

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

@Composable
fun rememberLocationProvider(): FusedLocationProviderClient {
    val context = LocalContext.current
    return LocationServices.getFusedLocationProviderClient(context)
}

@Composable
fun rememberLocationPermissionLauncher(
    onResult: (Boolean) -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onResult
    )
    return launcher
}

@Composable
fun rememberCurrentLocation(
    fusedLocationProviderClient: FusedLocationProviderClient,
    permissionGranted: Boolean
): State<Location?> {
    return produceState<Location?>(initialValue = null, permissionGranted) {
        if (permissionGranted) {
            val cancellationTokenSource = CancellationTokenSource()
            try {
                val location = fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).await()
                value = location
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

fun getPlaceNameFromLocation(context: Context, latitude: Double, longitude: Double): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val thoroughfare = address.thoroughfare  // 街道名称
            val subThoroughfare = address.subThoroughfare  // 街道号码
            val locality = address.locality  // 市/县
            val subAdminArea = address.subAdminArea  // 区/县
            val adminArea = address.adminArea  // 省/州
            val country = address.countryName  // 国家
            val featureName = address.featureName  // 地标名称

            // 构建更详细的位置描述
            val addressParts = mutableListOf<String>()

            // 添加国家信息
            if (!country.isNullOrEmpty()) {
                addressParts.add(country)
            }

            // 添加省/州信息
            if (!adminArea.isNullOrEmpty() && adminArea != locality) {
                addressParts.add(adminArea)
            }

            // 添加市/县信息
            if (!locality.isNullOrEmpty()) {
                addressParts.add(locality)
            }

            // 添加区/县信息
            if (!subAdminArea.isNullOrEmpty() && subAdminArea != locality) {
                addressParts.add(subAdminArea)
            }

            // 添加街道信息（如果可用）
            if (!thoroughfare.isNullOrEmpty() && !subThoroughfare.isNullOrEmpty()) {
                addressParts.add("$thoroughfare $subThoroughfare")
            } else if (!thoroughfare.isNullOrEmpty()) {
                addressParts.add(thoroughfare)
            } else if (!featureName.isNullOrEmpty() && featureName != locality) {
                addressParts.add(featureName)
            }

            if (addressParts.isNotEmpty()) {
                addressParts.joinToString(" ")
            } else {
                "${latitude}, ${longitude}"
            }
        } else {
            "${latitude}, ${longitude}"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "${latitude}, ${longitude}"
    }
}

// 扩展函数以支持await
suspend fun com.google.android.gms.tasks.Task<android.location.Location>.await(): android.location.Location? {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener {
            continuation.resume(it)
        }
        addOnFailureListener {
            continuation.resume(null)
        }
        // 如果需要取消操作，可以在这里添加取消逻辑
    }
}