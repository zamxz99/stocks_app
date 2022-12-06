package com.example.stocksapp.stocks.domain

import com.squareup.moshi.Json

data class Stock(
    val ticker: String,
    val name: String,
    val currency: String,
    @Json(name = "current_price_cents")
    val currentPriceCents: Int,
    @Json(name = "quantity")
    private val _quantity: Int?,
    @Json(name = "current_price_timestamp")
    val timestamp: Int
) {
    val quantity: Int
        get() = _quantity ?: 0
}
