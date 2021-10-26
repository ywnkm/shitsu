package net.ywnkm.shitsu

import kotlin.coroutines.cancellation.CancellationException

public interface Cancelable {

    public fun cancel(cause: CancellationException? = null)
}
