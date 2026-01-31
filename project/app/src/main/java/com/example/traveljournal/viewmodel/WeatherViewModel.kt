package com.example.traveljournal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveljournal.data.remote.RemoteDataSource
import com.example.traveljournal.data.model.WeatherResponse
import com.example.traveljournal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    fun getWeatherData(query: String? = null, days: Int = 7) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            when (val result = remoteDataSource.getWeather(query, days)) {
                is Resource.Success -> {
                    _weatherState.value = WeatherUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _weatherState.value = WeatherUiState.Error(result.message ?: "Unknown error")
                }
                is Resource.Loading -> {
                    _weatherState.value = WeatherUiState.Loading
                }
            }
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}