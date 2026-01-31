package com.example.traveljournal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveljournal.domain.model.Trip
import com.example.traveljournal.domain.usecase.DeleteTripUseCase
import com.example.traveljournal.domain.usecase.GetTripsUseCase
import com.example.traveljournal.domain.usecase.SearchTripsUseCase
import com.example.traveljournal.util.ImageUtils
import com.example.traveljournal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTripsUseCase: GetTripsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val searchTripsUseCase: SearchTripsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            getTripsUseCase().collect { tripList ->
                _trips.value = tripList
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadTrips()
        } else {
            searchTrips(query)
        }
    }

    private fun searchTrips(query: String) {
        viewModelScope.launch {
            searchTripsUseCase(query).collect { tripList ->
                _trips.value = tripList
            }
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState(loading = true)
                // 删除图片文件
                ImageUtils.deleteImageFromInternalStorage(context, trip.photoPath)
                deleteTripUseCase(trip)
                _uiState.value = UiState(loading = false)
            } catch (e: Exception) {
                _uiState.value = UiState(error = e.message)
            }
        }
    }
    
    data class UiState(
        val loading: Boolean = false,
        val error: String? = null
    )
}