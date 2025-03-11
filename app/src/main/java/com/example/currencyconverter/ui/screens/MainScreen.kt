package com.example.currencyconverter.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.currencyconverter.R
import com.example.currencyconverter.ui.model.CurrencyDropdownMenu
import com.example.currencyconverter.viewmodel.CurrencyViewModel
import com.example.currencyconverter.viewmodel.UiState

@SuppressLint("DefaultLocale")
fun calculateResult(
    amountText: String,
    rates: Map<String, Double>,
    selectedTarget: String,
    invalidInputMsg: String
): String {
    val amount = amountText.toDoubleOrNull() ?: return invalidInputMsg
    val rate = rates[selectedTarget] ?: 1.0
    return String.format("%.2f", amount * rate)
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrencyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var amountText by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    val targetCurrenciesList  by viewModel.selectedCurrencies.collectAsState()
    val targetCurrencies = targetCurrenciesList.joinToString(separator = ",")

    val invalidMsg = stringResource(id = R.string.invalid_input)

    // Track the user’s selected/typed base and target currencies
    val defaultBase = stringResource(id = R.string.eur)
    var selectedBase by remember { mutableStateOf(defaultBase) }
    val defaultTarget = stringResource(id = R.string.usd)
    var selectedTarget by remember { mutableStateOf(defaultTarget) }

    LaunchedEffect(selectedBase) {
        if (viewModel.hasLoadedRates && selectedBase in targetCurrenciesList) {
            viewModel.loadRates(baseCurrency = selectedBase, targetCurrencies = targetCurrencies)
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        // Button to fetch the currency rates
        Button(onClick = { viewModel.loadRates(baseCurrency = selectedBase, targetCurrencies = targetCurrencies) }) {
            Text(
                text = stringResource(R.string.fetch_rates),
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is UiState.Success -> {
                val rates = (uiState as UiState.Success).rates
                // Sort currency codes alphabetically
                val currencies = rates.keys.toList().sorted()

                // Dropdown for Base Currency: clear result when changed
                CurrencyDropdownMenu(
                    label = stringResource(R.string.base_currency),
                    value = selectedBase,
                    options = currencies,
                    onValueChange = { newValue ->
                        selectedBase = newValue.uppercase()
                        result = "" // clear the result on change
                    },
                    onOptionSelected = { option ->
                        selectedBase = option.uppercase()
                        result = "" // clear the result on change
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for Target Currency: clear result when changed
                CurrencyDropdownMenu(
                    label = stringResource(R.string.target_currency),
                    value = selectedTarget,
                    options = currencies,
                    onValueChange = { newValue ->
                        selectedTarget = newValue.uppercase()
                        result = "" // clear result on change
                    },
                    onOptionSelected = { option ->
                        selectedTarget = option.uppercase()
                        result = "" // clear result on change
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // TextField for entering the amount
                TextField(
                    value = amountText,
                    onValueChange = { newValue ->
                        amountText = newValue
                        result = calculateResult(amountText, rates, selectedTarget, invalidMsg)
                    },
                    label = { Text(stringResource(R.string.amount), style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                // "Calculate" button: performs the result calculation on press.
                Button(
                    onClick = {
                        result = calculateResult(amountText, rates, selectedTarget, invalidMsg)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.calculate),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Display result only if it is not empty.
                if (result.isNotEmpty()) {
                    if (result == stringResource(id = R.string.invalid_input)) {
                        Text(
                            text = stringResource(R.string.invalid_input),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.input_warning),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Text(
                            text = stringResource(
                                R.string.conversion_result_text,
                                amountText,
                                selectedBase,
                                result,
                                selectedTarget
                            )
                        )
                    }
                }
            }

            is UiState.Loading -> {
                // Display Loading message on API call
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.loading_rates), style = MaterialTheme.typography.bodyLarge)
            }

            is UiState.Error -> {
                // Error message and Currency dropdown option
                CurrencyDropdownMenu(
                    label = stringResource(R.string.base_currency),
                    value = selectedBase,
                    options = targetCurrenciesList,
                    onValueChange = { newValue ->
                        selectedBase = newValue.uppercase()
                    },
                    onOptionSelected = { option ->
                        selectedBase = option.uppercase()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.failed_to_load),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            is UiState.Idle -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.press_the_button_to_fetch_rates), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}