package com.example.currencyconverter.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.currencyconverter.network.CurrencyApiService

sealed interface UiState {
    data class Success(val rates: Map<String, Double>) : UiState
    object Error : UiState
    object Loading : UiState
    object Idle : UiState
}

class CurrencyViewModel : ViewModel() {
    private val apiService = CurrencyApiService.getInstance()
    private val apiKey = "cur_live_C4Qifveto1sVmro4DdePh1Vppi6BSb27gTyRgMcM"

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> get() = _uiState

    var hasLoadedRates by mutableStateOf(false)
        private set

    fun loadRates(baseCurrency: String, targetCurrencies: String = "") {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = if (targetCurrencies.isNotEmpty()) {
                    apiService.getLatestRates(apiKey, baseCurrency, targetCurrencies)
                } else {
                    apiService.getLatestRates(apiKey, baseCurrency)
                }
                Log.d("CurrencyViewModel", "Response: $response")

                val rates = response.data.mapValues { it.value.value }
                _uiState.value = UiState.Success(rates)

                hasLoadedRates = true
            } catch (e: Exception) {
                _uiState.value = UiState.Error
            }
        }
    }

    private val _selectedCurrencies = MutableStateFlow<List<String>>(listOf("EUR", "GBP", "USD"))
    val selectedCurrencies: StateFlow<List<String>> get() = _selectedCurrencies

    fun setSelectedCurrencies(currencies: List<String>) {
        _selectedCurrencies.value = currencies
    }
}