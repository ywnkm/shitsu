package net.ywnkm.shitsu.plugin

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content
import net.ywnkm.shitsu.settings.ShitsuConfig

public object CommonProvider : ShitsuPluginProvider {
    override fun provide(): List<ShitsuPlugin> = listOf(CommonPlugin)
}

public object CommonPlugin : ShitsuPlugin() {

    override val defaultPermit: PermitLevel
        get() = PermitLevel.ALL

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        engine {
            requestTimeout = 20000
        }
    }

    private val token = ShitsuConfig["glot.token"] ?: ""

    init {
        onEvent<GroupMessageEvent> {
            if (token.isEmpty()) return@onEvent
            val content = message.content
            if (!content.startsWith("!glot kt")) return@onEvent
            val c = content.substringAfter("!glot kt")
            val res: GlotResponse = try {
                client.post("https://glot.io/api/run/kotlin/latest") {
                    header("Authorization","Token $token")
                    contentType(ContentType.Application.Json)
                    body = GlotData.new("main.kt",c)
                }
            } catch (e: Throwable) {
                subject.sendMessage(At(sender) + "\n" + (e.message ?: "error"))
                return@onEvent
            }
            when {
                res.error.isNotEmpty() -> {
                    val message = buildMessageChain {
                        + At(sender)
                        + "\n"
                        + res.stderr
                        + "\n"
                        + res.error
                    }
                    subject.sendMessage(At(sender) + "\n" + message)
                }
                else -> {
                    subject.sendMessage(At(sender) + "\n" + res.stdout)
                }
            }
        }
    }
}

@Serializable
public data class GlotData(
    val files: List<GlotFile>
) {
    @Serializable
    public data class GlotFile(
        val name: String,
        val content: String
    )

    public companion object {

        public fun new(name: String, content: String): GlotData {
            return GlotData(listOf(GlotFile(name, content)))
        }
    }
}

@Serializable
public data class GlotResponse(
    val stdout: String,
    val stderr: String,
    val error: String
)
