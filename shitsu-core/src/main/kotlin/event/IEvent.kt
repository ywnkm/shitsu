package net.ywnkm.shitsu.event

import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public interface IEvent<T> {

    public fun subscribe(context: CoroutineContext = EmptyCoroutineContext, handler: EventHandler<T>): EventListener<T>

    public fun unsubscribe(handler: EventHandler<T>)

    public operator fun invoke(value: T, context: CoroutineContext = EmptyCoroutineContext): Job

    // region

    public operator fun plus(
            pair: Pair<CoroutineContext, EventHandler<T>>
    ): EventListener<T> = subscribe(pair.first, pair.second)

    public operator fun plus(
            handler: EventHandler<T>
    ): EventListener<T> = this + (EmptyCoroutineContext to handler)

    public operator fun minusAssign(
            handler: EventHandler<T>
    ): Unit = unsubscribe(handler)

    // endregion
}
