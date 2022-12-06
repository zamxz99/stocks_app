package com.example.stocksapp.stocks.ui.model

import com.example.stocksapp.stocks.domain.Stock
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

data class StockUIModel(
    val name: String,
    val quantity: Int,
    val lastUpdated: String,
    val currency: String,
    val price: Double,
    val total: Double
)

private val timestampFormat = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.US)

fun Stock.toStockUIModel(): StockUIModel {
    val priceBD = BigDecimal.valueOf(currentPriceCents.toLong())
        .setScale(2, RoundingMode.HALF_UP)
        .divide(BigDecimal.valueOf(100L))

    val totalBD = priceBD.multiply(quantity.toBigDecimal())

    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp * 1000L
    }

    return StockUIModel(
        name = "$ticker - $name",
        quantity = quantity,
        currency = Currency.getInstance(currency).symbol,
        lastUpdated = timestampFormat.format(calendar.time),
        price = priceBD.toDouble(),
        total = totalBD.toDouble()
    )
}