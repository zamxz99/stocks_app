package com.example.stocksapp.api

import androidx.annotation.StringRes

sealed class Resource<T>(val data: T?) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(
        data: T? = null,
        val code: Int,
        val body: String? = null,
        @StringRes val message: Int
    ) : Resource<T>(data)
}