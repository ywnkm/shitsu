package net.ywnkm.shitsu.plugin.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.ywnkm.shitsu.plugin.ShitsuPlugin
import net.ywnkm.shitsu.plugin.ShitsuPluginProvider
import net.ywnkm.shitsu.settings.ShitsuConfig
import net.ywnkm.shitsu.utils.BotTemplate
import net.ywnkm.shitsu.utils.internal.compileKotlin
import net.ywnkm.shitsu.utils.invoke
import net.ywnkm.shitsu.utils.io.deleteDir
import org.intellij.lang.annotations.Language
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

internal object MasterProvider : ShitsuPluginProvider {
    override fun provide(): List<ShitsuPlugin> = listOf(MasterCommand)
}

internal object MasterCommand : ShitsuPlugin() {

    override val defaultPermit: PermitLevel
        get() = PermitLevel.MASTER

    init {
        onEvent<GroupMessageEvent> {
            println("from master group message event: ${message.content}")
        }

        onEvent<FriendMessageEvent> {
            println("from master message event: ${message.content}")
        }

        onEvent<GroupMessageEvent> {
            val content = message.content
            if (!content.startsWith("!bot")) return@onEvent
            val c = content.substringAfter("!bot")
            invokeBotT(this, c)
        }
    }
}

internal val ShitsuConfig.kotlincPath: String?
    get() = get("kotlinc")


internal const val DefaultBtPackage: String = "net.ywnkm.shitsu.bt"

@Language("kt")
internal fun bt(
    content: String,
    packageName: String = DefaultBtPackage,
    className: String = "Fff"
) = """
package $packageName

import net.ywnkm.shitsu.utils.*
import net.mamoe.mirai.*
import net.mamoe.mirai.message.data.*
import kotlinx.coroutines.*
import kotlin.coroutines.*
import net.mamoe.mirai.event.events.*

class $className : BotTemplate {
    
    override suspend operator fun GroupMessageEvent.invoke() {
        $content
    }
}

"""

internal suspend fun invokeBotT(messageEvent: GroupMessageEvent, content: String) {
    coroutineScope {
        launch(Dispatchers.IO) {
            val cp = File(".").listFiles()?.firstOrNull {
                it.name.matches("shitsu-core.+\\.jar".toRegex())
            }?.absolutePath
                // shitsu_config.yml for test
                ?: ShitsuConfig["test.jarPath"]
                ?: kotlin.run {
                    System.err.println("can not find jar file used for kotlin compiler class path." +
                            "failed to compile $content")
                    return@launch
                }
            val pn = "net.zz"
            val cn = "Fff"
            val c = bt(content,pn,cn)
            val file = File("$cn.kt")
            fun clear() {
                file.delete()
                File("META-INF").deleteDir()
                File(pn.substringBefore(".")).deleteDir()
            }
            file.outputStream().use {
                it.write(c.toByteArray())
            }
            val s = compileKotlin(file.absolutePath, "-cp",cp)
            if(s.isNotEmpty()) {
                messageEvent.subject.sendMessage(s)
                if (s.contains("error:")) {
                    clear()
                    return@launch
                }
            }
            val dir = File(".")
            val classLoader = URLClassLoader(arrayOf(dir.toURI().toURL()))
            val kclass = classLoader.loadClass("$pn.$cn").kotlin
            val instance = kclass.primaryConstructor?.call()
            try {
                (instance as? BotTemplate)?.invoke(messageEvent)
            } catch (e: Throwable) {
                messageEvent.subject.sendMessage(e.message.toString())
            }
            clear()
            classLoader.close()
        }
    }
}
