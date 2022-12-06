package com.example.stocksapp.api

import com.example.stocksapp.R
import com.example.stocksapp.app.CoroutineContextProvider
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.lang.Exception
import java.net.ConnectException
import java.net.UnknownHostException

abstract class NetworkBoundResource<ResponseType, ResultType>(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val fetchScope: CoroutineScope = CoroutineScope(coroutineContextProvider.IO)
) {

    protected abstract suspend fun createCall(): Response<ResponseType>
    protected abstract suspend fun processResponse(response: ResponseType): ResultType?

    private suspend fun fetch(): Resource<ResultType> =
        try {
            val rawResponse = createCall()
            if (rawResponse.isSuccessful) {
                val data = rawResponse.body()?.let { processResponse(it) }
                Resource.Success(data)
            } else {
                // potentially process the error body if we did know the contract
                Resource.Error(
                    body = rawResponse.errorBody()?.toString(),
                    code = rawResponse.code(),
                    message = R.string.error_retrieving_data
                )
            }
        } catch (e: Exception) {
            Resource.Error(
                code = UNKNOWN_HTTP_CODE,
                message = getErrorResourceString(e)
            )
        }

    fun asFlow(): Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        emit(fetch())
    }.flowOn(coroutineContextProvider.IO)

    // have different error messages for different error cases
    private fun getErrorResourceString(e: Exception) =
        when (e) {
            is JsonEncodingException -> R.string.error_malformed_data
            is ConnectException -> R.string.error_internet_connect
            is UnknownHostException -> R.string.error_internet_connect
            else -> R.string.error_retrieving_data
        }

    companion object {
        const val UNKNOWN_HTTP_CODE = -1
    }
}