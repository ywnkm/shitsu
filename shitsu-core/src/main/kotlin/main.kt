package net.ywnkm.shitsu

import kotlinx.coroutines.delay
import net.ywnkm.shitsu.event.STEvent
import net.ywnkm.shitsu.event.internal.STEventImpl


public suspend fun main() {

    val event: STEvent<Int> = STEventImpl("233")

    val eventJob = event.subscribe {
        println("subscribe call $it")
        delay(1000)
        println(it)
    }

    val job = event(300)
    delay(20)
    eventJob.cancel()
    println("cancel job")
    event(456)
    Thread.sleep(20000)

    println("233")
}
