package com.example.stocksapp.stocks.ui.model

import androidx.annotation.StringRes

data class StatusUIModel(
    val isLoading: Boolean,
    val hasValidData: Boolean = true,
    @StringRes
    val message: Int? = null
)