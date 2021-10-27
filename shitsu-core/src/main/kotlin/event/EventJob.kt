package net.ywnkm.shitsu.event

import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlin.coroutines.cancellation.CancellationException

public interface EventJob<T> : EventHandlerScope {

    public val currentJob: Job?

    public val state: EventState

    public val isIntercepted: Boolean
        get() = state is EventState.Waiting

    override fun intercept(cancelJob: Boolean)

    override fun cancel(cause: CancellationException?)

    /**
     * resume handler to [EventState.Waiting] only if [state] is [EventState.Intercepted]
     *
     * otherwise, it will not work
     */
    public fun resume()

    public fun invokeOnCancel(block: () -> Unit): DisposableHandle

    public fun invokeOnIntercept(block: () -> Unit): DisposableHandle

}
