package org.kia.marshaler.impl

import org.kia.exception.ArgsException
import org.kia.exception.code.ErrorCode
import org.kia.marshaler.AbstractArgumentMarshaler

class IntArgumentMarshaler(override var value: Int = 0) : AbstractArgumentMarshaler<Int>() {
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