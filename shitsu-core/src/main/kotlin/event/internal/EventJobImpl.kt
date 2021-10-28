package net.ywnkm.shitsu.event.internal

import net.ywnkm.shitsu.event.EventHandlerScope
import net.ywnkm.shitsu.event.EventJob

internal class EventJobImpl<T>(
    val event: EventImpl<T>,
    val handler: suspend EventHandlerScope.(T) -> Unit
) : EventJob<T>
