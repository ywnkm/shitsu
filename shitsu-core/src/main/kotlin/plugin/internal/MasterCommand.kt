package net.ywnkm.shitsu.plugin.internal

import net.ywnkm.shitsu.plugin.ShitsuPlugin
import net.ywnkm.shitsu.plugin.ShitsuPluginProvider

internal object MasterProvider : ShitsuPluginProvider {
    override fun provide(): List<ShitsuPlugin> = listOf(MasterCommand)
}

internal object MasterCommand : ShitsuPlugin() {

    init {



    }
}