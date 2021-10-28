package net.ywnkm.shitsu.event.internal

import kotlinx.coroutines.DisposableHandle
import net.ywnkm.shitsu.event.STEventJob

internal class EventCancelHandlerDisposable(
    val job: STEventJob<*>,
    val onCancel: () -> Unit
) : DisposableHandle {
    override fun dispose() {
        job.remove(this)
    }
}

internal class EventInterceptDisposable(
    val job: STEventJob<*>,
    val onIntercept: () -> Unit
) : DisposableHandle {

    override fun dispose() {
        job.remove(this)
    }
}
