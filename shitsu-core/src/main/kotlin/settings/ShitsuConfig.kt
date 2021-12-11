package net.ywnkm.shitsu.settings

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.ywnkm.shitsu.event.EventHandlerScope
import net.ywnkm.shitsu.event.EventJob
import net.ywnkm.shitsu.event.IEvent
import net.ywnkm.shitsu.utils.ShitsuExperimental
import net.ywnkm.shitsu.utils.io.ReqLogger
import net.ywnkm.shitsu.utils.io.logger
import net.ywnkm.shitsu.utils.toJsonString
import org.yaml.snakeyaml.Yaml
import java.io.File


public open class ShitsuConfig constructor(
    configFilePath: String = "config/shitsu_config.yml"
) : IEvent<EventHandlerScope, ShitsuConfig, EventJob<ShitsuConfig>> by IEvent.newSimpleEvent(), ReqLogger {

    @PublishedApi
    internal var configMap: MutableMap<String, Any> = mutableMapOf()

    private val configFile: File = File(configFilePath)
    private val yaml = Yaml()

    init {
        // load config from file
        if (configFile.exists()) {
            val inputStream = configFile.inputStream()
            logger.verbose("read config file ...")
            try {
                configMap = yaml.load(inputStream)
            } catch (e: Exception) {
                logger.error("can not read config from ${configFile.absolutePath}", e)
            } finally {
                inputStream.close()
            }
            @Suppress("LeakingThis")
            invoke(this)
        }
    }

    public val pluginDir: File by lazy { File("plugins").apply { if(!exists()) mkdirs() } }

    public fun reload() {
        val inputStream = configFile.inputStream()
        try {
            configMap = yaml.load(inputStream)
            logger.verbose("config reloaded")
            invoke(this)
        } catch (e: Exception) {
            logger.error("config reload failed", e)
        } finally {
            inputStream.close()
        }
    }

    @OptIn(ShitsuExperimental::class, kotlinx.serialization.ExperimentalSerializationApi::class)
    public inline operator fun <reified T> get(key: String): T? {
        var map = configMap
        var result: Any?
        var t: T? = null
        val ks = key.split(".")
        for ((index,key2) in ks.withIndex())  {
            result = map[key2]
            @Suppress("Unchecked_Cast")
            val resultMap = (result as? MutableMap<String, Any>)
            if (resultMap != null && index != ks.size - 1) {
                map = resultMap
                continue
            }
            if (result === null) {
                logger.warning("can not find property $key from config")
                return null
            }

            kotlin.runCatching {
                t = result as T
            }.onFailure {
                if (T::class == Long::class) {
                    t = (result as Int).toLong() as T
                    return t
                }
                if (resultMap != null) {
                    t = Json.decodeFromString(resultMap.toJsonString())
                    return t
                }
                logger.warning("can not find property $key from config")
            }
        }
        return t
    }

    public inline fun <reified T> subscribe(propertyName: String, crossinline block: (T) -> Unit) {
        var old = get<T>(propertyName)
        subscribe {
            // get<T>(propertyName)?.let(block)

            val new = get<T>(propertyName)
            if (new != old) {
                new?.let(block)
                old = new
            }
        }
    }

    public companion object : ShitsuConfig()

}
