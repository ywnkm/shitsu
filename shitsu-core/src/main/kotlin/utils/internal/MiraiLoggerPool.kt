package net.ywnkm.shitsu.utils.internal

import net.mamoe.mirai.utils.MiraiLogger
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@PublishedApi
internal object MiraiLoggerPool {

    private val pool = mutableMapOf<KClass<*>, MiraiLogger>()

    @Synchronized
    fun getLogger(kClass: KClass<*>): MiraiLogger {
        return pool[kClass] ?: MiraiLogger.Factory.create(kClass).also {
            pool[kClass] = it
        }
    }

    operator fun getValue(p1: Any, p2: KProperty<*>): MiraiLogger = getLogger(p1::class)
}
