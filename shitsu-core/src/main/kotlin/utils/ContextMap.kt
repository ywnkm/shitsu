package net.ywnkm.shitsu.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

@ShitsuExperimental
public class ContextMap<K, V>(
        override val coroutineContext: CoroutineContext,
        private val delegate: MutableMap<K,V> = mutableMapOf()
): MutableMap<K,V> by delegate, CoroutineScope {

    override fun get(key: K): V? = runBlocking(coroutineContext) {
        delegate.get(key)
    }

    override fun put(key: K, value: V): V? = runBlocking(coroutineContext) {
        delegate.put(key, value)
    }

}
