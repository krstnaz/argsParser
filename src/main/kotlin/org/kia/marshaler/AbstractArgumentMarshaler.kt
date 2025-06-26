package org.kia.marshaler

abstract class AbstractArgumentMarshaler<T> {
    abstract var value: T

    abstract fun set(currentArgument: Iterator<String>? = null): Boolean

    fun get() = value
}