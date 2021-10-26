package net.ywnkm.shitsu.Event

public typealias EventHandler<T> = suspend EventHandlerScope.(T) -> Unit

