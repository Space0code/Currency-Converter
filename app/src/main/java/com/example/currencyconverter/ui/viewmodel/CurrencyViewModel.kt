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
    private val apiKey = "cur_live_EYjNXOVxitqCgxrJzW9tjrAdaCDooWAqeg5VBeoJ"

    // For conversion on Main Screen.
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> get() = _uiState

    var hasLoadedRates by mutableStateOf(false)
        private set

    var amountText by mutableStateOf("")
        private set

    var result by mutableStateOf("")
        private set

    var selectedBase by mutableStateOf("EUR")
        private set

    var selectedTarget by mutableStateOf("USD")
        private set

    // For the currencies the user wants available in the main dropdown.
    private val _selectedCurrencies = MutableStateFlow<List<String>>(listOf("EUR", "GBP", "USD"))
    val selectedCurrencies: StateFlow<List<String>> get() = _selectedCurrencies

    // For all available currencies (for the Currency List Screen).
    private val _allCurrencies = MutableStateFlow<List<String>>(emptyList())
    val allCurrencies: StateFlow<List<String>> get() = _allCurrencies

    fun setSelectedCurrencies(currencies: List<String>) {
        _selectedCurrencies.value = currencies
    }

    fun setAllCurrencies(currencies: List<String>) {
        _allCurrencies.value = currencies
    }

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

    // This function loads the full list of currencies and updates _allCurrencies.
    fun loadAllRates(baseCurrency: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getLatestRates(apiKey, baseCurrency)
                Log.d("CurrencyViewModel", "Response (all rates): $response")
                val rates = response.data.mapValues { it.value.value }
                _allCurrencies.value = rates.keys.toList().sorted()
            } catch (e: Exception) {
                _allCurrencies.value = emptyList()
            }
        }
    }

    // UI event handlers to update state.
    fun onAmountTextChanged(newValue: String) {
        amountText = newValue
        result = "" // clear previous result on input change
    }

    fun onSelectedBaseChanged(newValue: String) {
        selectedBase = newValue.uppercase()
        result = ""
    }

    fun onSelectedTargetChanged(newValue: String) {
        selectedTarget = newValue.uppercase()
        result = ""
    }

    fun onCalculateClicked(invalidInputMsg: String) {
        val state = _uiState.value
        if (state is UiState.Success) {
            calculateConversion(state.rates, invalidInputMsg, _selectedCurrencies.value)
        } else {
            result = invalidInputMsg
        }
    }

    private fun calculateConversion(
        rates: Map<String, Double>,
        invalidInputMsg: String,
        targetCurrenciesList: List<String>
    ) {
        if (selectedBase in targetCurrenciesList && selectedTarget in targetCurrenciesList) {
            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                result = invalidInputMsg
            } else {
                val rate = rates[selectedTarget] ?: 1.0
                result = String.format("%.2f", amount * rate)
            }
        } else {
            result = invalidInputMsg
        }
    }
}