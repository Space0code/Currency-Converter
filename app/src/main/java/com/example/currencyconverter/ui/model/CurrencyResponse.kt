package com.example.currencyconverter.model

data class CurrencyResponse(
    val meta: MetaData,
    val data: Map<String, CurrencyRate>
)

data class MetaData(
    val last_updated_at: String
)

data class CurrencyRate(
    val code: String,
    val value: Double
)