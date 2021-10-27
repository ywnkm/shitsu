package net.ywnkm.shitsu.event.internal

import net.ywnkm.shitsu.event.EventListener
import net.ywnkm.shitsu.event.EventState
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

internal class EventListenerImpl<T>(
        val event: STEventImpl<T>
) : EventListener<T> {
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
    override val state: EventState
        get() = TODO("Not yet implemented")

    override fun intercept(cancelJob: Boolean) {
        TODO("Not yet implemented")
    }

    override fun cancel(cause: CancellationException?) {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }
}