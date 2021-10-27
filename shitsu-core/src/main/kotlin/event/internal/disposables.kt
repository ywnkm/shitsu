package net.ywnkm.shitsu.event.internal

import kotlinx.coroutines.DisposableHandle
import net.ywnkm.shitsu.event.EventJob

internal class EventCancelHandlerDisposable(
    val job: EventJob<*>,
    val onCancel: () -> Unit
) : DisposableHandle {
    override fun dispose() {

    }
}

internal class EventInterceptDisposable(
    val job: EventJob<*>,
    val onIntercept: () -> Unit
) : DisposableHandle {

    override fun dispose() {
        TODO("Not yet implemented")
    }
}
