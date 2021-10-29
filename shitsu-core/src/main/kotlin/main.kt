package net.ywnkm.shitsu

import kotlinx.coroutines.delay
import net.ywnkm.shitsu.event.STEvent
import net.ywnkm.shitsu.event.internal.STEventJobImpl


public suspend fun main() {

    val event = STEvent.newSTEvent<Int>("233")

    val eventJob = event.subscribe {
        println("subscribe call $it")
        delay(1000)
        println(it)
        this as STEventJobImpl<*>
        println("1$state")
        cancel()
        println("2$state")
        delay(1000)
        println("3$state")
        println("subscribe end $it")
    }

    eventJob.invokeOnCancel {
        println("cnacelededed")
    }

    val event2 = STEvent.newSimpleEvent<Int>()

    event2.subscribe {
        println("event2 subsribe call $it")
    }

    val job = event(300)
    //eventJob.cancel()
    event(456)
    delay(2500)
    event2(666)
    event(2500).join()
    delay(200)

    println(event)
    println(event2)
    println("233")
}
