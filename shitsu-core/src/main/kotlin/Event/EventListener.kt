package net.ywnkm.shitsu.Event

import net.ywnkm.shitsu.Cancelable
import kotlin.coroutines.cancellation.CancellationException

public interface EventListener<T> : EventHandlerScope {

    public val state: EventState

    public val isIntercepted: Boolean
        get() = state == EventState.INTERCEPTED

    override fun intercept()

    override fun cancel(cause: CancellationException?)

    /**
     * resume handler to [EventState.WAITING] only if [state] is [EventState.INTERCEPTED]
     *
     * if [state] is [EventState.CANCELED] or [EventState.CANCELLING], it will not work
     */
    public fun resume()
}
