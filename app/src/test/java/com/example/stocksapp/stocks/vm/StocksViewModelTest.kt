package com.example.stocksapp.stocks.vm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.stocksapp.api.StocksApiService
import com.example.stocksapp.stocks.data.StocksRepository
import com.example.stocksapp.stocks.domain.Stock
import com.example.stocksapp.stocks.domain.StocksResponse
import com.example.stocksapp.stocks.util.TestCoroutineContextProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import com.example.stocksapp.R
import com.squareup.moshi.JsonEncodingException
import io.mockk.coVerify
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class StocksViewModelTest {

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val coroutineContextProvider = TestCoroutineContextProvider()
    private val testContext = coroutineContextProvider.testDispatchers
    private val stocksApiService = mockk<StocksApiService>()
    private val stocksRepository = StocksRepository(coroutineContextProvider, stocksApiService)


    @Test
    fun `get nonEmpty stocks, successful`() = runTest(testContext) {
        coEvery { stocksApiService.getStocks() } returns Response.success(
            StocksResponse(listOf(STOCK_1))
        )

        val viewModel = StocksViewModel(coroutineContextProvider, stocksRepository)

        viewModel.stocks.test {
            val uiModels = awaitItem()
            assertEquals(1, uiModels.size)
            val uiModel = uiModels.first()
            assertEquals("${STOCK_1.ticker} - ${STOCK_1.name}", uiModel.name)
            assertEquals(STOCK_1.quantity, uiModel.quantity)
        }

        viewModel.status.test {
            val status = awaitItem()
            assertFalse(status.isLoading)
            assertTrue(status.hasValidData)
            assertNull(status.message)
        }

        coVerify {
            stocksApiService.getStocks()
        }
    }

    @Test
    fun `get empty list of stocks, successful`() = runTest(testContext) {
        coEvery { stocksApiService.getStocksEmpty() } returns Response.success(
            StocksResponse(emptyList())
        )

        val viewModel = StocksViewModel(coroutineContextProvider, stocksRepository).also {
            it.getStocksEmpty()
        }

        viewModel.stocks.test {
            val uiModels = awaitItem()
            assertEquals(0, uiModels.size)
        }

        viewModel.status.test {
            val status = awaitItem()
            assertFalse(status.isLoading)
            assertFalse(status.hasValidData)
            assertEquals(R.string.empty_state, status.message)
        }

        coVerify(exactly = 0) {
            stocksApiService.getStocks()
        }

        coVerify {
            stocksApiService.getStocksEmpty()
        }
    }

    @Test
    fun `get nonEmpty of stocks, failed`() = runTest(testContext) {
        coEvery { stocksApiService.getStocks() } returns
            Response.error(400, "".toResponseBody("plain/text".toMediaType()))

        val viewModel = StocksViewModel(coroutineContextProvider, stocksRepository)

        viewModel.stocks.test {
            val uiModels = awaitItem()
            assertEquals(0, uiModels.size)
        }

        viewModel.status.test {
            val status = awaitItem()
            assertFalse(status.isLoading)
            assertFalse(status.hasValidData)
            assertEquals(R.string.error_retrieving_data, status.message)
        }

        coVerify {
            stocksApiService.getStocks()
        }
    }

    @Test
    fun `get malformed of stocks, failed`() = runTest(testContext) {
        coEvery { stocksApiService.getStocksMalformed() } throws JsonEncodingException(null)

        val viewModel = StocksViewModel(coroutineContextProvider, stocksRepository).also {
            it.getStocksMalformed()
        }

        viewModel.stocks.test {
            val uiModels = awaitItem()
            assertEquals(0, uiModels.size)
        }

        viewModel.status.test {
            val status = awaitItem()
            assertFalse(status.isLoading)
            assertFalse(status.hasValidData)
            assertEquals(R.string.error_malformed_data, status.message)
        }

        coVerify {
            stocksApiService.getStocksMalformed()
        }
    }

    @Test
    fun `get nonEmpty stocks, loading`() = runTest(testContext) {
        coEvery { stocksApiService.getStocks() } returns Response.success(
            StocksResponse(listOf(STOCK_1))
        )

        val viewModel = StocksViewModel(coroutineContextProvider, stocksRepository)

        viewModel.status.test {
            val status = awaitItem()
            assertTrue(status.isLoading)
            assertTrue(status.hasValidData)
            assertEquals(null, status.message)

            val status2 = awaitItem()
            assertFalse(status2.isLoading)
            assertTrue(status2.hasValidData)
            assertEquals(null, status.message)
        }

        coVerify {
            stocksApiService.getStocks()
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