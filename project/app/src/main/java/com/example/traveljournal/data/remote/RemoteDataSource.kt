package com.example.traveljournal.data.remote

import com.example.traveljournal.data.remote.api.ExchangeRateApiService
import com.example.traveljournal.data.remote.api.GeocodeApiService
import com.example.traveljournal.data.remote.api.WeatherApiService
import com.example.traveljournal.data.model.ExchangeRateResponse
import com.example.traveljournal.data.model.WeatherResponse
import com.example.traveljournal.util.Resource
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val geocodeApiService: GeocodeApiService,
    private val exchangeRateApiService: ExchangeRateApiService,
    private val weatherApiService: WeatherApiService
) {
    suspend fun geocodeAddress(address: String, apiKey: String): Resource<DoubleArray?> {
        return try {
            val response = geocodeApiService.geocodeAddress(address, apiKey)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.status == "OK" && !responseBody.results.isNullOrEmpty()) {
                    val location = responseBody.results.first().geometry?.location
                    if (location != null) {
                        Resource.Success(doubleArrayOf(location.lat ?: 0.0, location.lng ?: 0.0))
                    } else {
                        Resource.Error("Location data not found")
                    }
                } else {
                    Resource.Error(responseBody?.status ?: "Unknown error")
                }
            } else {
                Resource.Error("Network request failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
    
    suspend fun reverseGeocode(lat: Double, lng: Double, apiKey: String): Resource<String?> {
        return try {
            val latlng = "$lat,$lng"
            val response = geocodeApiService.reverseGeocode(latlng, apiKey)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.status == "OK" && !responseBody.results.isNullOrEmpty()) {
                    Resource.Success(responseBody.results.first().formattedAddress)
                } else {
                    Resource.Error(responseBody?.status ?: "Unknown error")
                }
            } else {
                Resource.Error("Network request failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getExchangeRates(currency: String = "CNY", encoding: String = "json"): Resource<ExchangeRateResponse> {
        return try {
            val response = exchangeRateApiService.getExchangeRates(currency, encoding)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Resource.Success(responseBody)
                } else {
                    Resource.Error("Response body is null")
                }
            } else {
                Resource.Error("Network request failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getWeather(query: String? = null, days: Int = 7, encoding: String = "json"): Resource<WeatherResponse> {
        return try {
            val response = weatherApiService.getWeatherForecast(query, days, encoding)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Resource.Success(responseBody)
                } else {
                    Resource.Error("Response body is null")
                }
            } else {
                Resource.Error("Network request failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

}