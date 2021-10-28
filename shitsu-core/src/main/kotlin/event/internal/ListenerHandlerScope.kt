package net.ywnkm.shitsu.event.internal

import net.ywnkm.shitsu.event.EventHandlerScope
import net.ywnkm.shitsu.event.STEventJob

internal class ListenerHandlerScope(
    val delegate: STEventJob<*>
) : EventHandlerScope by delegate
