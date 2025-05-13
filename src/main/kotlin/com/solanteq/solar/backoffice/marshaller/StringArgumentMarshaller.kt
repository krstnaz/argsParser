package com.solanteq.solar.backoffice.marshaller

/**
 * @since %CURRENT_VERSION%
 */
class StringArgumentMarshaller(override var value: String = "") : ArgumentMarshaller<String>() {
    override fun set(value: String) {
        this.value = value
    }
}