package net.ywnkm.shitsu.event.internal

import net.ywnkm.shitsu.event.EventHandlerScope
import net.ywnkm.shitsu.event.EventJob

internal class ListenerHandlerScope(
    val delegate: EventJob<*>
) : EventHandlerScope by delegate
