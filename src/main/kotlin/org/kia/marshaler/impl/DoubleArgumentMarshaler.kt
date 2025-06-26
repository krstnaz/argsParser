package org.kia.marshaler.impl

import org.kia.exception.ArgsException
import org.kia.exception.code.ErrorCode
import org.kia.marshaler.AbstractArgumentMarshaler

class DoubleArgumentMarshaler(override var value: Double = 0.0) : AbstractArgumentMarshaler<Double>() {
    override fun set(currentArgument: Iterator<String>?): Boolean {
        try {
            val currentValue = currentArgument?.next()
            this.value = parseDouble(currentValue)
            return true
        } catch (e: NoSuchElementException) {
            throw ArgsException(
                message = "the iteration has no more elements",
                errorCode = ErrorCode.MISSING_DOUBLE
            )
        } catch (e: NumberFormatException) {
            throw ArgsException(
                message = "Invalid double format for $value",
                errorCode = ErrorCode.INVALID_DOUBLE
            )
        }
    }

    private fun parseDouble(value: String?) = value?.toDoubleOrNull().let {
        it ?: throw NumberFormatException()
    }
}