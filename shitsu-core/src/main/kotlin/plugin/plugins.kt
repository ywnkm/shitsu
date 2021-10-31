package net.ywnkm.shitsu.plugin

import net.mamoe.mirai.event.Event

public inline fun <reified T : Event> (suspend T.() -> Unit).asShitsuPlugin(
    moduleName: String = "null",
    permit: ShitsuPlugin.PermitLevel = ShitsuPlugin.PermitLevel.ALL,
): ShitsuPlugin = object : ShitsuPlugin() {

    init {
        onEvent(moduleName = moduleName, permit = permit,block = this@asShitsuPlugin)
    }

}
