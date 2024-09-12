package com.example.ocr.eventBus

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

class EventBus private constructor() {
    private val _events = MutableSharedFlow<Any>()
    val events: SharedFlow<Any> = _events.asSharedFlow()

    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    fun publish(scope: CoroutineScope, event: Any) {
        scope.launch {
            _events.emit(event)
        }
    }

    inline fun <reified T : Any> subscribe(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        crossinline onEvent: suspend (T) -> Unit
    ): Job = scope.launch(dispatcher) {
        events.filterIsInstance<T>().collect { onEvent(it) }
    }

    companion object {
        @Volatile
        private var instance: EventBus? = null

        fun getInstance(): EventBus =
            instance ?: synchronized(this) {
                instance ?: EventBus().also { instance = it }
            }
    }
}