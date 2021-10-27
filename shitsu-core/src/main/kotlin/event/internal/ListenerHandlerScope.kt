package net.ywnkm.shitsu.event.internal

import net.ywnkm.shitsu.event.EventHandlerScope
import net.ywnkm.shitsu.event.EventListener

internal class ListenerHandlerScope(
        val delegate: EventListener<*>
) : EventHandlerScope by delegate
