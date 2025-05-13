package com.solanteq.solar.backoffice.marshaller

/**
 * @since %CURRENT_VERSION%
 */
abstract class ArgumentMarshaller<T> {
    abstract var value: T

    fun set(value: T) {
        this.value = value
    }

    fun get() = value
}