package com.example.traveljournal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveljournal.data.model.ExchangeRate
import com.example.traveljournal.data.remote.RemoteDataSource
import com.example.traveljournal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ViewModel() {

    private val _exchangeRates = MutableStateFlow<List<ExchangeRate>>(emptyList())
    val exchangeRates: StateFlow<List<ExchangeRate>> = _exchangeRates

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchExchangeRates() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = remoteDataSource.getExchangeRates()) {
                is Resource.Success -> {
                    val rates = result.data?.data?.rates?.map { item ->
                        ExchangeRate(item.currency, item.rate)
                    } ?: emptyList()
                    _exchangeRates.value = rates
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {
                    // 已经设置了 _isLoading.value = true，所以这里不需要额外操作
                }
            }

            _isLoading.value = false
        }
    }
}