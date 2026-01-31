package com.example.traveljournal.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveljournal.data.remote.RemoteDataSource
import com.example.traveljournal.domain.model.Location
import com.example.traveljournal.domain.model.Trip
import com.example.traveljournal.domain.usecase.GetTripByIdUseCase
import com.example.traveljournal.domain.usecase.SaveTripUseCase
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
class AddEditTripViewModel @Inject constructor(
    private val saveTripUseCase: SaveTripUseCase,
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val remoteDataSource: RemoteDataSource,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _trip = MutableStateFlow(Trip(title = "", description = "", date = "", photoPath = null))
    val trip: StateFlow<Trip> = _trip.asStateFlow()

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _tripId: Long? = runCatching {
        (savedStateHandle["tripId"] as? String)?.toLongOrNull()
            ?: (savedStateHandle["tripId"] as? Long)
    }.getOrNull()

    private val tripId: Long? get() = _tripId

    init {
        _tripId?.let { id ->
            loadTrip(id)
        }
    }

    private fun loadTrip(id: Long) {
        viewModelScope.launch {
            getTripByIdUseCase(id).collect { trip: Trip? ->
                trip?.let {
                    _trip.value = it
                    _locations.value = it.locations
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _trip.value = _trip.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _trip.value = _trip.value.copy(description = description)
    }

    fun updateDate(date: String) {
        _trip.value = _trip.value.copy(date = date)
    }

    fun updatePhotoPath(photoPath: String?) {
        _trip.value = _trip.value.copy(photoPath = photoPath)
    }

    /**
     * 处理图片URI并保存到内部存储
     * @param uri 图片的URI
     */
    fun saveImageToInternalStorage(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = "正在保存图片...")
            try {
                val imagePath = ImageUtils.saveImageToInternalStorage(context, uri)
                if (imagePath != null) {
                    _trip.value = _trip.value.copy(photoPath = imagePath)
                    _uiState.value = _uiState.value.copy(message = "图片保存成功")
                } else {
                    _uiState.value = _uiState.value.copy(error = "图片保存失败")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    fun addLocation(location: Location) {
        val updatedLocations = _locations.value.toMutableList()
        updatedLocations.add(location)
        _locations.value = updatedLocations
        // 更新trip中的locations
        _trip.value = _trip.value.copy(locations = updatedLocations)
    }

    fun removeLocation(location: Location) {
        val updatedLocations = _locations.value.toMutableList()
        updatedLocations.remove(location)
        _locations.value = updatedLocations
        // 更新trip中的locations
        _trip.value = _trip.value.copy(locations = updatedLocations)
    }

    fun saveTrip() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState(loading = true)

                // 创建包含所有位置的完整Trip对象
                val tripToSave = _trip.value.copy(locations = _locations.value)

                val result = saveTripUseCase(tripToSave)
                _uiState.value = UiState(loading = false, success = result != -1L)
            } catch (e: Exception) {
                _uiState.value = UiState(loading = false, error = e.message)
            }
        }
    }
    
    // 通过地址获取地理坐标
    fun geocodeAddress(address: String, apiKey: String = "") {
        viewModelScope.launch {
            _uiState.value = UiState(loading = true, message = "正在获取位置信息...")

            when (val result = remoteDataSource.geocodeAddress(address, apiKey)) {
                is Resource.Success -> {
                    result.data?.let { coordinates ->
                        if (coordinates.size >= 2) {
                            val location = Location(
                                locationName = address,
                                tripId = _trip.value.id,
                                latitude = coordinates[0],
                                longitude = coordinates[1]
                            )
                            addLocation(location)
                            _uiState.value = UiState(message = "位置添加成功")
                        } else {
                            _uiState.value = UiState(error = "无法解析地址")
                        }
                    }
                }
                is Resource.Error -> {
                    _uiState.value = UiState(error = result.message)
                }
                is Resource.Loading -> {
                    _uiState.value = UiState(loading = true, message = "正在获取位置信息...")
                }
            }
        }
    }

    // 通过GPS获取当前位置
    fun addCurrentLocation(locationName: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(locationLoading = true, message = "正在添加位置...")
            try {
                val location = Location(
                    locationName = locationName,
                    tripId = _trip.value.id,
                    latitude = latitude,
                    longitude = longitude
                )
                addLocation(location)
                _uiState.value = _uiState.value.copy(
                    locationLoading = false,
                    message = "当前位置已添加"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    locationLoading = false,
                    error = e.message
                )
            }
        }
    }

    // 新增：用于直接启动位置获取流程的方法
    fun startLocationRetrieval() {
        _uiState.value = _uiState.value.copy(locationLoading = true, message = "正在获取位置...")
    }

    // 更新UI状态的函数
    fun updateUiMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    fun updateUiError(error: String) {
        _uiState.value = _uiState.value.copy(error = error)
    }

    fun updateUiLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(loading = loading)
    }

    fun updateLocationLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(locationLoading = loading)
    }

    data class UiState(
        val loading: Boolean = false,
        val locationLoading: Boolean = false,  // 新增：专门用于位置获取的加载状态
        val error: String? = null,
        val success: Boolean = false,
        val message: String? = null
    )
}