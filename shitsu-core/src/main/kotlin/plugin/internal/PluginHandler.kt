package net.ywnkm.shitsu.plugin.internal

import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.MessageEvent
import net.ywnkm.shitsu.plugin.ShitsuPlugin
import kotlin.reflect.KClass

@PublishedApi
internal class PluginHandler<T : Event> (
    val moduleName: String,
    var permitLevel: ShitsuPlugin.PermitLevel,
    val handler: suspend T.() -> Unit,
    val clazz: KClass<T>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PluginHandler<*>

        if (moduleName != other.moduleName) return false

        return true
    }

    override fun hashCode(): Int {
        return moduleName.hashCode()
    }

    companion object {

        internal inline operator fun <reified T : Event> invoke(
            moduleName: String,
            permitLevel: ShitsuPlugin.PermitLevel,
            noinline handler: suspend T.() -> Unit,
        ): PluginHandler<T> = PluginHandler(moduleName, permitLevel, handler, T::class)
    }
}
