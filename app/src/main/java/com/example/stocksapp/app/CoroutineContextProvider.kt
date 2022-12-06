package com.example.stocksapp.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {
    val Main: CoroutineContext
    val Default: CoroutineContext
    val IO: CoroutineContext
    val Unconfined: CoroutineContext
    val appScope: CoroutineScope
}

@Singleton
class CoroutineContextProviderImpl(
    override val Main: CoroutineContext = Dispatchers.Main,
    override val Default: CoroutineContext = Dispatchers.Default,
    override val IO: CoroutineContext = Dispatchers.IO,
    override val Unconfined: CoroutineContext = Dispatchers.Unconfined,
    override val appScope: CoroutineScope = CoroutineScope(SupervisorJob())
) : CoroutineContextProvider