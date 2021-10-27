package net.ywnkm.shitsu.event

public typealias EventHandler<T> = suspend EventHandlerScope.(T) -> Unit

