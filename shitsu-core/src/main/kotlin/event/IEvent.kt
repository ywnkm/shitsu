package net.ywnkm.shitsu.event

import kotlinx.coroutines.Job
import net.ywnkm.shitsu.event.internal.EventImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public interface IEvent<out R : EventHandlerScope, T, out J : EventJob<T>> {

    public fun subscribe(
        context: CoroutineContext = EmptyCoroutineContext,
        handler: EventHandler<R, T>
    ): J

    public fun unsubscribe(handler: EventHandler<R, T>)

    public operator fun invoke(value: T, context: CoroutineContext = EmptyCoroutineContext): Job

    // region

    public operator fun plus(
        pair: Pair<CoroutineContext, EventHandler<R, T>>
    ): J = subscribe(pair.first, pair.second)

    public operator fun plus(
        handler: EventHandler<R, T>
    ): J = this + (EmptyCoroutineContext to handler)

    public operator fun plusAssign(
        pair: Pair<CoroutineContext, EventHandler<R, T>>
    ): Unit = Unit.also { plus(pair) }

    public operator fun plusAssign(
        handler: EventHandler<R, T>
    ): Unit = Unit.also { plus(handler) }

    public operator fun minusAssign(
        handler: EventHandler<R, T>
    ): Unit = unsubscribe(handler)

    // endregion

    public interface Companion {

        /**
         * create a simple [IEvent], all operations like [invoke], [subscribe], [unsubscribe] are in a single thread dispatcher
         */
        public fun <T> newSimpleEvent(): IEvent<EventHandlerScope, T, EventJob<T>> = EventImpl()
    }

    public companion object Tools : Companion
}
