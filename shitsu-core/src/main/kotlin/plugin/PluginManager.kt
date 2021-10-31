package net.ywnkm.shitsu.plugin

import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.ywnkm.shitsu.internal.ShitsuR
import net.ywnkm.shitsu.utils.JarUtils
import net.ywnkm.shitsu.utils.ShitsuExperimental
import net.ywnkm.shitsu.utils.io.ReqLogger
import java.io.File

public open class PluginManager private constructor(
    pluginDirPath: String = "plugins"
) : ReqLogger {

    public val pluginDir: File = File(pluginDirPath).also { it.mkdirs() }

    internal val plugins = mutableListOf<ShitsuPlugin>()

    @OptIn(ShitsuExperimental::class)
    public fun loadPlugin() {
        JarUtils.loadJarFromDir(pluginDir).onObject(::loadPluginFromProvider)
    }

    public fun loadPluginFromProvider(provider: ShitsuPluginProvider) {
        provider.provide().forEach(::registerPlugin)
    }

    @Suppress("Unchecked_Cast")
    public fun registerPlugin(plugin: ShitsuPlugin) {
        val bot = ShitsuR.curBot ?: return
        val masterId = ShitsuR.masterId
        for (handler in plugin.handlers) {
            when {
                handler.clazz.qualifiedName == GroupMessageEvent::class.qualifiedName -> {
                    val h = (handler.handler as? suspend GroupMessageEvent.() -> Unit) ?: return
                    bot.eventChannel.subscribeAlways<GroupMessageEvent> {
                        val senderPermit = sender.permission
                        @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
                        when(handler.permitLevel) {
                            ShitsuPlugin.PermitLevel.GROUP_ADMINISTRATOR -> {
                                if (senderPermit.level < MemberPermission.ADMINISTRATOR.level
                                    && sender.id != masterId) return@subscribeAlways
                            }
                            ShitsuPlugin.PermitLevel.GROUP_OWNER -> {
                                if (senderPermit.level < MemberPermission.OWNER.level
                                    && sender.id != masterId) return@subscribeAlways
                            }
                            ShitsuPlugin.PermitLevel.MASTER -> {
                                if (sender.id != masterId) return@subscribeAlways
                            }
                        }
                        h()
                    }
                }

                else -> {
                    // todo
                    val g = handler.handler as? suspend Event.() -> Unit ?: return
                    val gg: suspend Event.(Event) -> Unit = { g() }
                    bot.eventChannel.subscribeAlways(eventClass = handler.clazz, handler = gg)
                }

            }
        }
        plugins.add(plugin)
    }

    public companion object : PluginManager()

}

