package net.ywnkm.shitsu.event

import kotlinx.coroutines.Job
import net.ywnkm.shitsu.event.internal.STEventImpl

public interface STEvent<T> : IEvent<STEventHandlerScope, T, STEventJob<T>> {

    public val eventName: String

    public fun remove(eventJob: STEventJob<T>)

    public operator fun get(eventJob: STEventJob<T>): Job?

    public companion object : IEvent.Companion {

        public fun <T> newSTEvent(eventName: String): STEvent<T> {
            return STEventImpl(eventName)
        }
    }
}
