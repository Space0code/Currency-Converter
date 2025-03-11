package com.example.currencyconverter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.currencyconverter.viewmodel.CurrencyViewModel
import com.example.currencyconverter.viewmodel.UiState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.currencyconverter.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListScreen(
    viewModel: CurrencyViewModel,
    navController: androidx.navigation.NavController
) {
    // Get the full list of currencies from the API response if available,
    // otherwise use an empty list.
    val uiState by viewModel.uiState.collectAsState()

    val randomCurrency = stringResource(R.string.eur)
    LaunchedEffect(Unit) {
        viewModel.loadRates(randomCurrency)
    }

    val currencies = when (uiState) {
        is UiState.Success -> (uiState as UiState.Success).rates.keys.toList().sorted()
        else -> emptyList()
    }
    // Use a mutable set to track which currencies the user has selected.
    val selectedCurrencies by viewModel.selectedCurrencies.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(R.string.select_currencies),
                    style = MaterialTheme.typography.headlineMedium,
                ) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text= stringResource(R.string.check_the_currencies_you_want_available), style=MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Save the selection to the ViewModel
                //viewModel.setSelectedCurrencies(selectedCurrencies.toList())
                navController.navigateUp()
            }) {
                Text(
                    text = stringResource(R.string.save_selection),
                    style = MaterialTheme.typography.labelMedium,
                    )
            }

            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(currencies) { currency ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val checked = selectedCurrencies.contains(currency)
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                val newSelection = if (isChecked) {
                                    selectedCurrencies + currency
                                } else {
                                    selectedCurrencies - currency
                                }
                                viewModel.setSelectedCurrencies(newSelection)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(currency)
                    }
                }
            }

        }
    }
}