package net.ywnkm.shitsu

import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.ywnkm.shitsu.internal.ShitsuR
import net.ywnkm.shitsu.plugin.CommonProvider
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
        initConfig(bot)
        ShitsuR.curBot = bot
        ShitsuR.masterId = ShitsuConfig["bot.master"] ?: 0
        ShitsuConfig.get<List<Long>>("groups")?.let {
            ShitsuR.groups += it
        }
        bot.login()
        PluginManager.loadPluginFromProvider(MasterProvider)
        PluginManager.loadPluginFromProvider(CommonProvider)
        PluginManager.loadPlugin()
        bot.join()
    }

    private fun initConfig(bot: Bot) {
        ShitsuR.curBot = bot
        ShitsuR.masterId = ShitsuConfig["bot.master"] ?: 0
        ShitsuConfig.get<List<Long>>("groups")?.let {
            ShitsuR.groups += it
        }
    }

}
