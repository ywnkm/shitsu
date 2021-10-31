package net.ywnkm.shitsu.plugin

public fun interface ShitsuPluginProvider {

    public fun provide(): List<ShitsuPlugin>
}
