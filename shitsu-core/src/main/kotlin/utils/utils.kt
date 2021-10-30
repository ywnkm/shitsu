package net.ywnkm.shitsu.utils

public fun Map<String, Any>.toJsonString(): String = buildString {
    fun sr(str: String): String = "\"$str\""
    append("{")
    var i = 0
    fun `append,`() {
        if (i != size -1) append(",")
    }
    for ((key, value) in this@toJsonString) {
        when(value) {

            is String -> {
                append(sr(key)).append(":").append(sr(value))
                `append,`()
            }
            is Number -> {
                append(sr(key)).append(":").append(value)
                `append,`()
            }
            is Map<*, *> -> {
                @Suppress("Unchecked_Cast")
                value as Map<String, Any>
                append(sr(key)).append(":").append(value.toJsonString())
                `append,`()
            }
            is ArrayList<*> -> {
                append(sr(key)).append(":")
                append("[")
                for ((index, o) in value.withIndex()) {
                    when(o) {
                        is String -> {
                            append(sr(o))
                        }
                        is Number -> append(o)
                        is Map<*, *> -> {
                            @Suppress("Unchecked_Cast")
                            o as Map<String, Any>
                            append(o.toJsonString())
                        }
                    }
                    if (index != value.size - 1) append(",")
                }
                append("]")
                `append,`()
            }
        }
        i++
    }
    append("}")
}
