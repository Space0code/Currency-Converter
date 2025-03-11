package com.example.currencyconverter.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.currencyconverter.ui.navigation.BottomNavigationApp
import com.example.currencyconverter.ui.theme.CurrencyConverterTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyConverterTheme {
                BottomNavigationApp()
            }
        }
    }
}


