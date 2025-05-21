package com.solanteq.solar.backoffice.marshaller

import com.solanteq.solar.backoffice.exception.ArgsException

/**
 * @since %CURRENT_VERSION%
 */
class IntArgumentMarshaller(override var value: Int = 0) : ArgumentMarshaller<Int>() {
    override fun set(value: String) {
        try {
            this.value = Integer.parseInt(value)
        } catch (e: NumberFormatException) {
            throw ArgsException("Invalid integer format for $value")
        }
    }
}