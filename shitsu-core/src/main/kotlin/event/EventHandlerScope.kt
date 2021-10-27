package net.ywnkm.shitsu.event

import kotlinx.coroutines.CoroutineScope
import net.ywnkm.shitsu.Cancelable
import kotlin.coroutines.cancellation.CancellationException

public interface EventHandlerScope : Cancelable, CoroutineScope {

    /**
     * @see EventListener.intercept
     */
    public fun intercept(cancelJob: Boolean = false)

    /**
     * @see EventListener.cancel
     */
    override fun cancel(cause: CancellationException?)

}
