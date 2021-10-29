package net.ywnkm.shitsu.event.internal

import kotlinx.coroutines.*
import net.ywnkm.shitsu.event.EventHandler
import net.ywnkm.shitsu.event.EventJob
import net.ywnkm.shitsu.event.IEvent
import net.ywnkm.shitsu.event.EventHandlerScope
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

internal class EventImpl<T> : IEvent<EventHandlerScope, T, EventJob<T>>, EventHandlerScope, CoroutineScope {

    private val sDispatcher = Executors.newSingleThreadExecutor {
        Thread(it).apply { isDaemon = true }
    }.asCoroutineDispatcher()

    override val coroutineContext = SupervisorJob() + sDispatcher

    val jobs = mutableListOf<EventJobImpl<T>>()

    override fun subscribe(
        context: CoroutineContext,
        handler: EventHandler<EventHandlerScope, T>
    ): EventJobImpl<T> {
        val eventJob = EventJobImpl(this, handler)
        launch {
            jobs.add(eventJob)
        }
        return eventJob
    }

    override fun unsubscribe(handler: EventHandler<EventHandlerScope, T>) {
        launch {
            jobs.removeIf {
                it.handler == handler
            }
        }
    }

    override fun invoke(value: T, context: CoroutineContext): Job {
        return launch {
            var exception: Throwable? = null
            for (job in jobs) {
                try {
                    job.handler(this@EventImpl, value)
                } catch (e: Throwable) {
                    exception = e
                }
            }
            exception?.let { throw it }
        }
    }

}
