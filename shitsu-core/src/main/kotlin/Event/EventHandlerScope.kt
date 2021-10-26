package net.ywnkm.shitsu.Event

import net.ywnkm.shitsu.Cancelable
import kotlin.coroutines.cancellation.CancellationException

public interface EventHandlerScope : Cancelable {

    public fun intercept()

    override fun cancel(cause: CancellationException?)

}