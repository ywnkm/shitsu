package net.ywnkm.shitsu.internal

import net.mamoe.mirai.Bot

internal object ShitsuR {

    var curBot: Bot? = null
    @Synchronized set

    var masterId: Long = 0

    val groups: MutableList<Long> = mutableListOf()
}