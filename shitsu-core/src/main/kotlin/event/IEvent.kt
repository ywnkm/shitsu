package net.ywnkm.shitsu.event

import kotlinx.coroutines.Job
import net.ywnkm.shitsu.event.internal.EventImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public interface IEvent<out R : EventHandlerScope, T, out J : EventJob<T>> {

    public fun subscribe(
        context: CoroutineContext = EmptyCoroutineContext,
        handler: suspend R.(T) -> Unit
    ): J

    public fun unsubscribe(handler: suspend R.(T) -> Unit)

    public operator fun invoke(value: T, context: CoroutineContext = EmptyCoroutineContext): Job

    // region

    public operator fun plus(
        pair: Pair<CoroutineContext, suspend R.(T) -> Unit>
    ): J = subscribe(pair.first, pair.second)

    public operator fun plus(
        handler: suspend R.(T) -> Unit
    ): J = this + (EmptyCoroutineContext to handler)

    public operator fun plusAssign(
        pair: Pair<CoroutineContext, suspend R.(T) -> Unit>
    ): Unit = Unit.also { plus(pair) }

    public operator fun plusAssign(
        handler: suspend R.(T) -> Unit
    ): Unit = Unit.also { plus(handler) }

    public operator fun minusAssign(
            handler: suspend R.(T) -> Unit
    ): Unit = unsubscribe(handler)

    // endregion

    public interface Companion {
        public fun <T> newSimpleEvent(): IEvent<EventHandlerScope, T, EventJob<T>> = EventImpl()
    }

    public companion object Tools : Companion
}
