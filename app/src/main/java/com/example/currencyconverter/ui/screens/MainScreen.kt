package com.example.currencyconverter.ui.screens

import android.annotation.SuppressLint
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrencyViewModel = viewModel()
) {
    // Observe view model states
    val uiState by viewModel.uiState.collectAsState()
    val targetCurrenciesList by viewModel.selectedCurrencies.collectAsState()

    // Get states managed in the ViewModel
    val amountText = viewModel.amountText
    val result = viewModel.result
    val selectedBase = viewModel.selectedBase
    val selectedTarget = viewModel.selectedTarget

    val targetCurrencies = targetCurrenciesList.joinToString(separator = ",")
    val invalidMsg = stringResource(id = R.string.invalid_input)

    // Reload rates when base currency changes (if already loaded)
    LaunchedEffect(selectedBase) {
        if (viewModel.hasLoadedRates && selectedBase in targetCurrenciesList) {
            viewModel.loadRates(baseCurrency = selectedBase, targetCurrencies = targetCurrencies)
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        // Button to fetch the currency rates
        Button(onClick = {
            viewModel.loadRates(baseCurrency = selectedBase, targetCurrencies = targetCurrencies)
        }) {
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

                // Dropdown for Base Currency: update state via ViewModel
                CurrencyDropdownMenu(
                    label = stringResource(R.string.base_currency),
                    value = selectedBase,
                    options = currencies,
                    onValueChange = { newValue ->
                        viewModel.onSelectedBaseChanged(newValue)
                    },
                    onOptionSelected = { option ->
                        viewModel.onSelectedBaseChanged(option)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for Target Currency: update state via ViewModel
                CurrencyDropdownMenu(
                    label = stringResource(R.string.target_currency),
                    value = selectedTarget,
                    options = currencies,
                    onValueChange = { newValue ->
                        viewModel.onSelectedTargetChanged(newValue)
                    },
                    onOptionSelected = { option ->
                        viewModel.onSelectedTargetChanged(option)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // TextField for entering the amount
                TextField(
                    value = amountText,
                    onValueChange = { newValue ->
                        viewModel.onAmountTextChanged(newValue)
                    },
                    label = { Text(stringResource(R.string.amount), style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                // "Calculate" button: triggers conversion via ViewModel
                Button(
                    onClick = { viewModel.onCalculateClicked(invalidMsg) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.calculate),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Display result if not empty
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
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.loading_rates), style = MaterialTheme.typography.bodyLarge)
            }

            is UiState.Error -> {
                // In error state, allow user to change the base currency
                CurrencyDropdownMenu(
                    label = stringResource(R.string.base_currency),
                    value = selectedBase,
                    options = targetCurrenciesList,
                    onValueChange = { newValue ->
                        viewModel.onSelectedBaseChanged(newValue)
                    },
                    onOptionSelected = { option ->
                        viewModel.onSelectedBaseChanged(option)
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