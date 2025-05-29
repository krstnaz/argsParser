package com.solanteq.solar.backoffice.marshaller

/**
 * @since %CURRENT_VERSION%
 */
class BooleanArgumentMarshaler(override var value: Boolean = false) : ArgumentMarshaler<Boolean>() {
    override fun set(currentArgument: Iterator<String>?): Boolean {
        this.value = true
        return true
    }
}