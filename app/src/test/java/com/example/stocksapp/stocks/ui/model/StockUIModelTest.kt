package com.example.stocksapp.stocks.ui.model

import com.example.stocksapp.stocks.domain.Stock
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.util.Calendar

class StockUIModelTest {

    @Test
    fun `get StockUIModel from Stock`() {
        val year = 2000
        val month = 2
        val date = 4
        val hourOfDay = 6
        val minute = 15
        val second = 30
        val amPM = "AM"

        val cal = Calendar.getInstance().apply {
            set(year, month -1, date, hourOfDay, minute, second)
        }

        val unixTime = (cal.timeInMillis / 1000).toInt()
        val expectedTimeFormat = "$month/$date/$year $hourOfDay:$minute:$second $amPM"
        val stock = Stock(
            ticker = "ticker",
            name = "name",
            currency = "USD",
            currentPriceCents = 2,
            _quantity = 3,
            timestamp = unixTime
        )

        val uiModel = stock.toStockUIModel()

        assertEquals("${stock.ticker} - ${stock.name}", uiModel.name)
        assertEquals(stock.quantity, uiModel.quantity)
        assertEquals("$", uiModel.currency)
        assertEquals(expectedTimeFormat, uiModel.lastUpdated)

        val price = BigDecimal.valueOf(0.02).setScale(2, HALF_UP)
        assertEquals(price, BigDecimal.valueOf(uiModel.price).setScale(2, HALF_UP))
        val total = BigDecimal.valueOf(0.06).setScale(2, HALF_UP)
        assertEquals(total, BigDecimal.valueOf(uiModel.total).setScale(2, HALF_UP))
    }
}