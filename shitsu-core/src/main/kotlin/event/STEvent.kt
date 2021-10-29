package net.ywnkm.shitsu.event

import kotlinx.coroutines.Job
import net.ywnkm.shitsu.event.internal.STEventImpl
import kotlin.reflect.KClass

public interface STEvent<T> : IEvent<STEventHandlerScope, T, STEventJob<T>> {

    public val eventName: String

    public fun remove(eventJob: STEventJob<T>)

    public operator fun get(eventJob: STEventJob<T>): Job?

    public companion object : IEvent.Companion {

        public fun <T : Any> newSTEvent(eventName: String, kClass: KClass<T>): STEvent<T> = STEventImpl(eventName, kClass)

        public inline fun <reified T : Any> newSTEvent(eventName: String): STEvent<T> = newSTEvent(eventName, T::class)

    }
}
