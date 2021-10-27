package net.ywnkm.shitsu.utils

import kotlinx.coroutines.DisposableHandle

public sealed interface DisposableList {

    public object Nil : DisposableList

    public class Cons(
        public val head: DisposableHandle,
        public val tail: DisposableList
    ) : DisposableList
}

public fun DisposableList.remove(disposableHandle: DisposableHandle): DisposableList = when(this) {
    DisposableList.Nil -> this
    is DisposableList.Cons -> {
        if (head == disposableHandle) tail
        else DisposableList.Cons(head, tail.remove(disposableHandle))
    }
}

public tailrec fun DisposableList.forEach(action: (DisposableHandle) -> Unit): Unit = when(this) {
    DisposableList.Nil -> Unit
    is DisposableList.Cons -> {
        action(head)
        tail.forEach(action)
    }
}

public inline fun <reified T : DisposableHandle> DisposableList.loopOn(
    crossinline action: (T) -> Unit
): Unit = forEach {
    if (it is T) action(it)
}
