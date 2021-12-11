package net.ywnkm.shitsu.utils.internal

import kotlinx.coroutines.suspendCancellableCoroutine
import net.ywnkm.shitsu.plugin.internal.kotlincPath
import net.ywnkm.shitsu.settings.ShitsuConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.concurrent.thread
import kotlin.coroutines.resume

private val kotlinExe: String = run {
    val on = System.getProperty("os.name").lowercase(Locale.getDefault())
    when {
        on.contains("windows") -> "kotlinc.bat"
        else -> "kotlinc"
    }
}

/**
 * @return error msg
 */
internal suspend fun compileKotlin(path: String, vararg options: String): String {
    val kotlincPath = ShitsuConfig.kotlincPath ?: return "kotlin compiler path not defined"
    val op = options.joinToString(" ")
    return suspendCancellableCoroutine {
        thread {
            try {
                val cmd = "${kotlincPath}$kotlinExe $path $op"
                val process = Runtime.getRuntime().exec(cmd)
                it.invokeOnCancellation {
                    process.destroy()
                }
                val result = StringBuilder()
                BufferedReader(InputStreamReader(process.errorStream)).use { br ->
                    var line: String
                    while ( br.readLine().also { line = it } !== null) {
                        result.append(line).append("\n")
                    }
                }

                it.resume(result.toString())
            } catch (e: Throwable) {
                it.resume(e.message ?: "error")
            }
        }
    }
}
