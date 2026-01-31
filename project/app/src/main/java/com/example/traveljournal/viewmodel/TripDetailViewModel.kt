package com.example.traveljournal.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveljournal.domain.model.Trip
import com.example.traveljournal.domain.usecase.DeleteTripUseCase
import com.example.traveljournal.domain.usecase.GetTripByIdUseCase
import com.example.traveljournal.util.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _tripId: Long = runCatching {
        (savedStateHandle["tripId"] as? String)?.toLongOrNull()
            ?: (savedStateHandle["tripId"] as? Long) ?: 0L
    }.getOrNull() ?: 0L

    private val tripId: Long get() = _tripId

    init {
        loadTrip()
    }

    private fun loadTrip() {
        if (tripId != 0L) {
            viewModelScope.launch {
                getTripByIdUseCase(tripId).collect { trip: Trip? ->
                    _trip.value = trip
                }
            }
        }
    }

    fun deleteTrip() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState(loading = true)
                _trip.value?.let { trip ->
                    // 删除图片文件
                    ImageUtils.deleteImageFromInternalStorage(context, trip.photoPath)
                    deleteTripUseCase(trip)
                    _uiState.value = UiState(loading = false, isDeleted = true)
                }
            } catch (e: Exception) {
                _uiState.value = UiState(loading = false, error = e.message)
            }
        }
    }
    
    data class UiState(
        val loading: Boolean = false,
        val error: String? = null,
        val isDeleted: Boolean = false
    )
}