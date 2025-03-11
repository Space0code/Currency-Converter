
package com.example.currencyconverter.network

import com.example.currencyconverter.model.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface CurrencyApiService {
    @GET("v3/latest")
    suspend fun getLatestRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") targetCurrencies: String
    ): CurrencyResponse

    @GET("v3/latest")
    suspend fun getLatestRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String,
    ): CurrencyResponse

    companion object {
        private const val BASE_URL = "https://api.currencyapi.com/"

        fun getInstance(): CurrencyApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CurrencyApiService::class.java)
        }
    }
}