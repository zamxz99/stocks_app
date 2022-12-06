package com.example.stocksapp.stocks.data

import com.example.stocksapp.api.NetworkBoundResource
import com.example.stocksapp.api.Resource
import com.example.stocksapp.api.StocksApiService
import com.example.stocksapp.app.CoroutineContextProvider
import com.example.stocksapp.stocks.domain.StocksResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StocksRepository @Inject constructor(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val stocksApiService: StocksApiService
) {

    fun <ResultType> getStocks(
        coroutineScope: CoroutineScope,
        transform: (StocksResponse) -> ResultType
    ): Flow<Resource<ResultType>> = object :
        NetworkBoundResource<StocksResponse, ResultType>(coroutineContextProvider, coroutineScope) {
        override suspend fun createCall(): Response<StocksResponse> = stocksApiService.getStocks()

        override suspend fun processResponse(response: StocksResponse): ResultType =
            transform(response)
    }.asFlow()

    fun <ResultType> getStocksEmpty(
        coroutineScope: CoroutineScope,
        transform: (StocksResponse) -> ResultType
    ): Flow<Resource<ResultType>> = object :
        NetworkBoundResource<StocksResponse, ResultType>(coroutineContextProvider, coroutineScope) {
        override suspend fun createCall(): Response<StocksResponse> =
            stocksApiService.getStocksEmpty()

        override suspend fun processResponse(response: StocksResponse): ResultType =
            transform(response)
    }.asFlow()

    fun <ResultType> getStocksMalformed(
        coroutineScope: CoroutineScope,
        transform: (StocksResponse) -> ResultType
    ): Flow<Resource<ResultType>> = object :
        NetworkBoundResource<StocksResponse, ResultType>(coroutineContextProvider, coroutineScope) {
        override suspend fun createCall(): Response<StocksResponse> =
            stocksApiService.getStocksMalformed()

        override suspend fun processResponse(response: StocksResponse): ResultType =
            transform(response)
    }.asFlow()
}