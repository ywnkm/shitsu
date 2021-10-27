package net.ywnkm.shitsu.event

import kotlinx.coroutines.CoroutineScope
import net.ywnkm.shitsu.Cancelable
import kotlin.coroutines.cancellation.CancellationException

public interface EventHandlerScope : Cancelable, CoroutineScope {

    /**
     * @see EventJob.intercept
     */
    public fun intercept(cancelJob: Boolean = false)

    /**
     * @see EventJob.cancel
     */
    override fun cancel(cause: CancellationException?)

}
