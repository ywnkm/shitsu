package net.ywnkm.shitsu.event

import kotlinx.coroutines.CoroutineScope
import net.ywnkm.shitsu.Cancelable
import kotlin.coroutines.cancellation.CancellationException

public interface STEventHandlerScope : EventHandlerScope, Cancelable, CoroutineScope {

    /**
     * @see STEventJob.intercept
     */
    public fun intercept(cancelJob: Boolean = false)

    /**
     * @see STEventJob.cancel
     */
    override fun cancel(cause: CancellationException?)
}
