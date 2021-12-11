package net.ywnkm.shitsu.utils

import net.mamoe.mirai.event.events.GroupMessageEvent

public fun interface BotTemplate {
    public suspend operator fun GroupMessageEvent.invoke()
}

public suspend fun BotTemplate.invoke(messageEvent: GroupMessageEvent) {
    messageEvent.apply {
        invoke()
    }
}
