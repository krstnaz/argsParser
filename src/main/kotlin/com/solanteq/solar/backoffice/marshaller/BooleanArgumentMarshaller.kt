package com.solanteq.solar.backoffice.marshaller

/**
 * @since %CURRENT_VERSION%
 */
class BooleanArgumentMarshaller(override var value: Boolean = false) : ArgumentMarshaller<Boolean>() {
    override fun set(value: String) {
        this.value = value.toBoolean()
    }
}