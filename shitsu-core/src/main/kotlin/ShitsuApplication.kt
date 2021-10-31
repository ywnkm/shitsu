package net.ywnkm.shitsu

import net.mamoe.mirai.BotFactory
import net.ywnkm.shitsu.internal.ShitsuR
import net.ywnkm.shitsu.plugin.PluginManager
import net.ywnkm.shitsu.plugin.internal.MasterProvider
import net.ywnkm.shitsu.settings.ShitsuConfig

public class ShitsuApplication {


    public suspend fun start() {

        val id = ShitsuConfig["bot.id"] ?: 0L

        val pass = ShitsuConfig.get<String>("bot.pass") ?: ""

        val bot = BotFactory.newBot(id,pass) {
            fileBasedDeviceInfo("config/device.json")
        }
        ShitsuR.curBot = bot
        bot.login()
        PluginManager.loadPluginFromProvider(MasterProvider)
        PluginManager.loadPlugin()
        bot.join()
    }

}
