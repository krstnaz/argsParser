package com.solanteq.solar.backoffice.marshaller

/**
 * @since %CURRENT_VERSION%
 */
abstract class ArgumentMarshaler<T> {
    abstract var value: T

    abstract fun set(currentArgument: Iterator<String>? = null): Boolean

    fun get() = value
}