package net.ywnkm.shitsu.event.internal

import kotlinx.coroutines.Job
import net.ywnkm.shitsu.event.EventHandler
import net.ywnkm.shitsu.event.EventListener
import net.ywnkm.shitsu.event.STEvent
import kotlin.coroutines.CoroutineContext

internal class STEventImpl<T> : STEvent<T> {

    override fun subscribe(context: CoroutineContext, handler: EventHandler<T>): EventListener<T> {
        TODO("Not yet implemented")
    }

    override fun unsubscribe(handler: EventHandler<T>) {
        TODO("Not yet implemented")
    }

    override fun invoke(value: T, context: CoroutineContext): Job {
        TODO("Not yet implemented")
    }
}