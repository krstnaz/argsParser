package com.solanteq.solar.backoffice.marshaller

import com.solanteq.solar.backoffice.exception.ArgsException
import com.solanteq.solar.backoffice.exception.ErrorCode

/**
 * @since %CURRENT_VERSION%
 */
class StringArgumentMarshaler(override var value: String = "") : ArgumentMarshaler<String>() {
    override fun set(currentArgument: Iterator<String>?): Boolean {
        try {
            this.value = currentArgument?.next() ?: ""
            return true
        } catch (e: NoSuchElementException) {
            throw ArgsException(
                message = "the iteration has no more elements",
                errorCode = ErrorCode.MISSING_STRING
            )
        }
    }
}