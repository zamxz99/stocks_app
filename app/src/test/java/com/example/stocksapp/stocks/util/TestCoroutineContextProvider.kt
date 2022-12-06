package com.example.stocksapp.stocks.util

import com.example.stocksapp.app.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineContextProvider : CoroutineContextProvider {
    val testDispatchers = StandardTestDispatcher()
    override val Main = testDispatchers
    override val Default = testDispatchers
    override val IO = testDispatchers
    override val Unconfined = testDispatchers
    override val appScope = CoroutineScope(testDispatchers)
}