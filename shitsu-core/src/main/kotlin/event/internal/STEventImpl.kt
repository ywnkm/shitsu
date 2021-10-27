package net.ywnkm.shitsu.event.internal

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
import net.ywnkm.shitsu.event.EventHandler
import net.ywnkm.shitsu.event.EventJob
import net.ywnkm.shitsu.event.EventState
import net.ywnkm.shitsu.event.STEvent
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

internal class STEventImpl<T>(
    override val eventName: String
) : STEvent<T> {

    private val context: CoroutineContext = SupervisorJob() + CoroutineName("[STEvent: $eventName]")

    private val mapDispatcher = Executors.newSingleThreadExecutor {
        Thread(it).apply { isDaemon = true }
    }.asCoroutineDispatcher()

    private val mapScope = CoroutineScope(context + mapDispatcher)

    private val scope = CoroutineScope(context)

    private val eventJobs: AtomicRef<MutableList<EventJobImpl<T>>> = atomic(mutableListOf())

    override fun subscribe(context: CoroutineContext, handler: EventHandler<T>): EventJob<T> {
        val eventJob = EventJobImpl(this, handler, context)
        eventJobs.update {
            it.apply { add(eventJob) }
        }
        return eventJob
    }

    override fun unsubscribe(handler: EventHandler<T>) {
        eventJobs.update { pre ->
            pre.apply {
                removeIf { it.handler == handler }
            }
        }
    }

    override fun invoke(value: T, context: CoroutineContext): Job {
        return scope.launch(context) {
            for (eventJob in eventJobs.value) {
                val state = eventJob.state
                when(state) {
                    is EventState.Cancelled,
                    is EventState.Cancelling,
                    is EventState.Intercepted -> continue
                }
                eventJob.currentJob = launch {
                    eventJob.handler.invoke(eventJob ,value)
                }
            }
        }
    }

    override fun remove(eventJob: EventJob<T>) {
        eventJobs.update {
            it.apply { remove(eventJob) }
        }
    }

    override fun get(eventJob: EventJob<T>): Job? = eventJob.currentJob
}
