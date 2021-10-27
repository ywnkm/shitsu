package net.ywnkm.shitsu.event

import kotlinx.coroutines.DisposableHandle
import net.ywnkm.shitsu.event.internal.EventCancelHandlerDisposable
import net.ywnkm.shitsu.event.internal.EventInterceptDisposable
import net.ywnkm.shitsu.utils.DisposableList
import net.ywnkm.shitsu.utils.loopOn
import net.ywnkm.shitsu.utils.remove

/*
public enum class EventState {
    WAITING, RUNNING, INTERCEPTED, CANCELLING, CANCELED
}
*/

public sealed class EventState {

    private var disposableList: DisposableList = DisposableList.Nil

    public fun from(state: EventState): EventState = this.apply {
        this.disposableList = state.disposableList
    }

    public fun with(disposableHandle: DisposableHandle): EventState = this.apply {
        this.disposableList = DisposableList.Cons(disposableHandle, this.disposableList)
    }

    public fun without(disposableHandle: DisposableHandle): EventState = this.apply {
        this.disposableList = this.disposableList.remove(disposableHandle)
    }

    public fun clear() {
        this.disposableList = DisposableList.Nil
    }

    public fun notifyCancellation() {
        disposableList.loopOn<EventCancelHandlerDisposable> {
            it.onCancel()
        }
    }

    public fun notifyIntercept() {
        disposableList.loopOn<EventInterceptDisposable> {
            it.onIntercept()
        }
    }

    public class Waiting : EventState()

    public class Running : EventState()

    public class Intercepted : EventState()

    public class Cancelling : EventState()

    public class Cancelled : EventState()

}
