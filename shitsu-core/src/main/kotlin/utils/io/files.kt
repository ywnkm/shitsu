package net.ywnkm.shitsu.utils.io

import java.io.File

public fun File.deleteDir(): Boolean {
    if (!exists()) return false
    if (isFile) return delete()
    val lf = listFiles() ?: return false
    for (file in lf) {
        file.deleteDir()
    }
    return delete()
}
