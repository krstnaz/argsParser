package com.solanteq.solar.backoffice.marshaller

import com.solanteq.solar.backoffice.exception.ArgsException
import com.solanteq.solar.backoffice.exception.ErrorCode

/**
 * @since %CURRENT_VERSION%
 */
class IntArgumentMarshaller(override var value: Int = 0) : ArgumentMarshaller<Int>() {
    override fun set(currentArgument: Iterator<String>?): Boolean {
        try {
            val currentValue = currentArgument?.next()
            this.value = Integer.parseInt(currentValue)
            return true
        } catch (e: NoSuchElementException) {
            throw ArgsException(
                message = "the iteration has no more elements",
                errorCode = ErrorCode.MISSING_INT
            )
        } catch (e: NumberFormatException) {
            throw ArgsException(
                message = "Invalid integer format for $value",
                errorCode = ErrorCode.INVALID_INT
            )
        }
    }
}