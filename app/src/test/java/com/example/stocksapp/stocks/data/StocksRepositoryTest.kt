package com.example.stocksapp.stocks.data

import app.cash.turbine.test
import com.example.stocksapp.api.Resource.Error
import com.example.stocksapp.api.Resource.Loading
import com.example.stocksapp.api.Resource.Success
import com.example.stocksapp.api.StocksApiService
import com.example.stocksapp.stocks.domain.Stock
import com.example.stocksapp.stocks.domain.StocksResponse
import com.example.stocksapp.stocks.util.TestCoroutineContextProvider
import com.squareup.moshi.JsonEncodingException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import com.example.stocksapp.R
import com.example.stocksapp.api.NetworkBoundResource.Companion.UNKNOWN_HTTP_CODE
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class StocksRepositoryTest {
    private val coroutineContextProvider = TestCoroutineContextProvider()
    private val testContext = coroutineContextProvider.testDispatchers
    private val stocksApiService = mockk<StocksApiService>()
    private val stocksRepository = StocksRepository(coroutineContextProvider, stocksApiService)

    @Test
    fun `get nonEmpty stocks, successful`() = runTest(testContext) {
        val stocks = listOf(STOCK_1)

        coEvery { stocksApiService.getStocks() } returns Response.success(
            StocksResponse(stocks)
        )

        stocksRepository.getStocks(this) {
            it.stocks
        }.test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Loading)
            val successResource = awaitItem()
            assertTrue(successResource is Success)
            assertEquals(stocks, successResource.data)

            awaitComplete()
        }
    }

    @Test
    fun `get nonEmpty stocks, failed`() = runTest(testContext) {
        coEvery { stocksApiService.getStocks() } returns Response.error(
            400, "".toResponseBody("plain/text".toMediaType())
        )

        stocksRepository.getStocks(this) {
            it.stocks
        }.test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Loading)
            val errorResource = awaitItem()
            assertTrue(errorResource is Error)
            val error = errorResource as Error
            assertEquals(400, error.code)
            assertEquals(R.string.error_retrieving_data, error.message)

            awaitComplete()
        }
    }

    @Test
    fun `get empty stocks, successful`() = runTest(testContext) {
        coEvery { stocksApiService.getStocks() } returns Response.success(
            StocksResponse(emptyList())
        )

        stocksRepository.getStocks(this) {
            it.stocks
        }.test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Loading)
            val successResource = awaitItem()
            assertTrue(successResource is Success)
            assertTrue(successResource.data?.isEmpty() == true)

            awaitComplete()
        }
    }

    @Test
    fun `get empty stocks, failed`() = runTest(testContext) {
        coEvery { stocksApiService.getStocks() } returns Response.error(
            400, "".toResponseBody("plain/text".toMediaType())
        )

        stocksRepository.getStocks(this) {
            it.stocks
        }.test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Loading)
            val errorResource = awaitItem()
            assertTrue(errorResource is Error)
            val error = errorResource as Error
            assertEquals(400, error.code)
            assertEquals(R.string.error_retrieving_data, error.message)

            awaitComplete()
        }
    }

    @Test
    fun `get malformed stocks, failed`() = runTest(testContext) {
        coEvery { stocksApiService.getStocksMalformed() } throws JsonEncodingException(null)

        stocksRepository.getStocksMalformed(this) {
            it.stocks
        }.test {
            val loadingResource = awaitItem()
            assertTrue(loadingResource is Loading)
            val errorResource = awaitItem()
            assertTrue(errorResource is Error)
            val error = errorResource as Error
            assertEquals(UNKNOWN_HTTP_CODE, error.code)
            assertEquals(R.string.error_malformed_data, error.message)

            awaitComplete()
        }
    }

    private companion object {
        val STOCK_1 = Stock(
            ticker = "ticker",
            name = "name",
            currency = "USD",
            currentPriceCents = 2,
            _quantity = 3,
            timestamp = (System.currentTimeMillis() / 1000L).toInt()
        )
    }
}