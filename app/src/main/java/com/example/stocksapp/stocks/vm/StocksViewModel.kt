package com.example.stocksapp.stocks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.R
import com.example.stocksapp.api.Resource.Error
import com.example.stocksapp.api.Resource.Loading
import com.example.stocksapp.api.Resource.Success
import com.example.stocksapp.app.CoroutineContextProvider
import com.example.stocksapp.stocks.data.StocksRepository
import com.example.stocksapp.stocks.domain.StocksResponse
import com.example.stocksapp.stocks.ui.model.StatusUIModel
import com.example.stocksapp.stocks.ui.model.StockUIModel
import com.example.stocksapp.stocks.ui.model.toStockUIModel
import com.example.stocksapp.stocks.vm.StocksViewModel.FetchType.EmptyData
import com.example.stocksapp.stocks.vm.StocksViewModel.FetchType.MalformedData
import com.example.stocksapp.stocks.vm.StocksViewModel.FetchType.NonEmptyData
import com.example.stocksapp.util.conflatedSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val stocksRepository: StocksRepository
) : ViewModel() {

    private var fetchJob: Job? = null
    private val _stocks = conflatedSharedFlow<List<StockUIModel>>()
    val stocks: Flow<List<StockUIModel>>
        get() = _stocks
    private val _status = conflatedSharedFlow<StatusUIModel>()
    val status: Flow<StatusUIModel>
        get() = _status

    init {
        getStocks()
    }

    fun getStocks() = getStocks(NonEmptyData)

    fun getStocksEmpty() = getStocks(EmptyData)

    fun getStocksMalformed() = getStocks(MalformedData)

    private fun getStocks(fetchType: FetchType) {
        fetchJob?.cancel()

        when (fetchType) {
            NonEmptyData -> stocksRepository.getStocks(viewModelScope, ::transform)
            EmptyData -> stocksRepository.getStocksEmpty(viewModelScope, ::transform)
            MalformedData -> stocksRepository.getStocksMalformed(viewModelScope, ::transform)
        }.also { flow ->
            fetchJob = viewModelScope.launch(coroutineContextProvider.Default) {
                flow.collect { resource ->
                    when (resource) {
                        is Error -> {
                            _stocks.emit(emptyList())
                            _status.emit(
                                StatusUIModel(
                                    isLoading = false,
                                    hasValidData = false,
                                    message = resource.message
                                )
                            )
                        }
                        is Loading -> _status.emit(StatusUIModel(isLoading = true))
                        is Success -> {
                            val data = resource.data ?: emptyList()
                            _stocks.emit(data)
                            _status.emit(
                                StatusUIModel(
                                    isLoading = false,
                                    message = if (data.isEmpty()) R.string.empty_state else null,
                                    hasValidData = data.isNotEmpty()
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun transform(response: StocksResponse): List<StockUIModel> =
        response.stocks.map { stock ->
            stock.toStockUIModel()
        }

    sealed class FetchType {
        object NonEmptyData : FetchType()
        object EmptyData : FetchType()
        object MalformedData : FetchType()
    }
}