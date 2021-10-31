package net.ywnkm.shitsu.event

public typealias EventHandler<R, T> = suspend R.(T) -> Unit
