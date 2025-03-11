package com.example.currencyconverter.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.currencyconverter.R
import com.example.currencyconverter.ui.screens.CurrencyListScreen
import com.example.currencyconverter.ui.screens.InfoScreen
import com.example.currencyconverter.ui.screens.MainScreen
import com.example.currencyconverter.viewmodel.CurrencyViewModel

@Composable
fun BottomNavigationApp() {
    val navController = rememberNavController()
    val currencyViewModel: CurrencyViewModel = viewModel()
    val bottomNavItems = listOf(
        BottomNavItem(stringResource(R.string.main), "main", Icons.Default.Home),
        BottomNavItem(stringResource(R.string.info), "info", Icons.Default.Info),
        BottomNavItem(stringResource(R.string.currencies), "currencyList", Icons.AutoMirrored.Filled.List)
    )

    Scaffold(
        topBar = { TitleBar() },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main") { MainScreen(viewModel = currencyViewModel) }
            composable("info") { InfoScreen(viewModel = currencyViewModel, navController = navController) }
            composable("currencyList") { CurrencyListScreen(viewModel = currencyViewModel, navController = navController) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar() {
    CenterAlignedTopAppBar(
        title = { Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        ) }
    )
}
