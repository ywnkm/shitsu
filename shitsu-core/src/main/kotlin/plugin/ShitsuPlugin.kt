package net.ywnkm.shitsu.plugin

import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.MessageEvent
import net.ywnkm.shitsu.plugin.internal.PluginHandler
import net.ywnkm.shitsu.utils.io.ReqLogger

public abstract class ShitsuPlugin : ReqLogger {

    // region plugin info

    public open val pluginName: String = "example plugin"
    public open val version: String = "1.0"
    public open val versionCode: Int = 1
    public open val desc: String = ""
    public open val defaultPermit: PermitLevel = PermitLevel.ALL

    // endregion

    @PublishedApi
    internal val handlers: MutableList<PluginHandler<out Event>> = mutableListOf()

    protected inline fun <reified T : Event> onEvent(
        moduleName: String = "null",
        permit: PermitLevel = defaultPermit,
        noinline block: suspend T.() -> Unit) {
        handlers += PluginHandler(moduleName, permit, block)
    }

    public enum class PermitLevel(public val value: Int) {
        ALL(0),

        GROUP_MEMBER(30),
        GROUP_ADMINISTRATOR(34),
        GROUP_OWNER(38),

        MASTER(666)
    }
}