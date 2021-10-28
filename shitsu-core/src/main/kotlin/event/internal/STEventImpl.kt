package net.ywnkm.shitsu.event.internal

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
import net.ywnkm.shitsu.event.*
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

    private val eventJobs: AtomicRef<MutableList<STEventJobImpl<T>>> = atomic(mutableListOf())

    override fun subscribe(
        context: CoroutineContext,
        handler: suspend STEventHandlerScope.(T) -> Unit
    ): STEventJob<T> {
        val eventJob = STEventJobImpl(this, handler, context)
        eventJobs.update {
            it.add(eventJob)
            it
        }
        return eventJob
    }

    override fun unsubscribe(handler: suspend STEventHandlerScope.(T) -> Unit) {
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
                eventJob.currentJob = launch(eventJob.coroutineContext) {
                    eventJob.handler(eventJob ,value)
                }
            }
        }
    }

    override fun remove(eventJob: STEventJob<T>) {
        if (eventJob !is STEventJobImpl) return
        eventJobs.getAndUpdate { jobs ->
            val index = jobs.indexOf(eventJob)
            if (index >= 0) jobs.removeAt(index)
            jobs
        }
    }

    override fun get(eventJob: STEventJob<T>): Job? = eventJob.currentJob
}
