package net.ywnkm.shitsu.utils.io

import net.mamoe.mirai.utils.MiraiLogger
import net.ywnkm.shitsu.utils.internal.MiraiLoggerPool

/**
 * provide a [logger]
 */
public interface ReqLogger

public inline val <reified T : ReqLogger> T.logger: MiraiLogger
    get() = MiraiLoggerPool.getLogger(T::class)
