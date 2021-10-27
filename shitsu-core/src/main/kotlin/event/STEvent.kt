package net.ywnkm.shitsu.event

import kotlinx.coroutines.Job
import net.ywnkm.shitsu.event.internal.STEventImpl

public interface STEvent<T> : IEvent<T> {

    public val eventName: String

    public fun remove(eventJob: EventJob<T>)

    public operator fun get(eventJob: EventJob<T>): Job?

    public companion object {

        public fun <T> newEvent(eventName: String): STEvent<T> {
            return STEventImpl(eventName)
        }
    }
}
