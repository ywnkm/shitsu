package net.ywnkm.shitsu.event.internal

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.atomicfu.updateAndGet
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import net.ywnkm.shitsu.event.EventHandler
import net.ywnkm.shitsu.event.EventJob
import net.ywnkm.shitsu.event.EventState
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

internal class EventJobImpl<T>(
    val event: STEventImpl<T>,
    val handler: EventHandler<T>,
    override val coroutineContext: CoroutineContext
) : EventJob<T> {

    override var currentJob: Job? = null
        internal set

    private val _state: AtomicRef<EventState> = atomic(EventState.Waiting())

    override val state: EventState
        get() = _state.value

    override fun intercept(cancelJob: Boolean) {
        fun check() = if (cancelJob) cancel() else Unit
        _state.getAndUpdate { pre ->
            when(pre) {
                is EventState.Running -> EventState.Intercepted().from(pre).also { check() }
                is EventState.Waiting -> EventState.Waiting().from(pre).also { check() }
                is EventState.Intercepted,
                is EventState.Cancelling,
                is EventState.Cancelled -> pre
            }
        }
    }

    override fun cancel(cause: CancellationException?) {
        event.remove(this)
        val job = event[this]
        _state.updateAndGet ug@ { pre ->
            val newState = if (job === null) {
                EventState.Cancelled().from(pre)
            } else EventState.Cancelling().from(pre)
            return@ug when(pre) {
                is EventState.Waiting,
                is EventState.Running,
                is EventState.Intercepted -> newState
                is EventState.Cancelling,
                is EventState.Cancelled -> pre
            }
        }
        job?.let {
            it.cancel(cause)
            it.invokeOnCompletion {
                _state.updateAndGet { pre ->
                    when(pre) {
                        is EventState.Cancelling -> EventState.Cancelled().from(pre)
                        else -> pre
                    }
                }
            }
        }
    }

    override fun resume() {
        _state.getAndUpdate { pre ->
            when(pre) {
                is EventState.Intercepted -> {
                    // todo

                    EventState.Waiting().from(pre)
                }
                else -> pre
            }
        }
    }

    override fun invokeOnCancel(block: () -> Unit): DisposableHandle {
        val disposable = EventCancelHandlerDisposable(this, block)
        val newState = _state.updateAndGet { pre ->
            when(pre) {
                is EventState.Waiting -> EventState.Waiting().from(pre).with(disposable)
                is EventState.Running -> EventState.Running().from(pre).with(disposable)
                is EventState.Intercepted -> EventState.Intercepted().from(pre).with(disposable)
                is EventState.Cancelling -> EventState.Cancelling().from(pre).with(disposable)
                is EventState.Cancelled -> pre
            }
        }
        (newState as? EventState.Cancelled)?.let {
            block()
        }
        return disposable
    }

    override fun invokeOnIntercept(block: () -> Unit): DisposableHandle {
        val disposable = EventInterceptDisposable(this, block)
        val newState = _state.updateAndGet { pre ->
            when(pre) {
                is EventState.Waiting -> EventState.Waiting().from(pre).with(disposable)
                is EventState.Running -> EventState.Running().from(pre).with(disposable)
                is EventState.Intercepted,
                is EventState.Cancelling,
                is EventState.Cancelled -> pre
            }
        }
        (newState as? EventState.Intercepted)?.let {
            block()
        }
        return disposable
    }
}
