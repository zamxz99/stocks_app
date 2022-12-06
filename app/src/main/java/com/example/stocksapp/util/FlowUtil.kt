package com.example.stocksapp.util

import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.MutableSharedFlow

/*
 * SharedFlow configured to only emit when there's data (similar to LiveData)
 */
inline fun <reified T> conflatedSharedFlow() = MutableSharedFlow<T>(
    replay = 1, onBufferOverflow = DROP_OLDEST
)
