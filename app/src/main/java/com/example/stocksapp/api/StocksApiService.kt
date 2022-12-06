package com.example.stocksapp.api

import com.example.stocksapp.stocks.domain.StocksResponse
import retrofit2.Response
import retrofit2.http.GET

interface StocksApiService {
    @GET("portfolio.json")
    suspend fun getStocks(): Response<StocksResponse>

    @GET("portfolio_empty.json")
    suspend fun getStocksEmpty(): Response<StocksResponse>

    @GET("portfolio_malformed.json")
    suspend fun getStocksMalformed(): Response<StocksResponse>
}