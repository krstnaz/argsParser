package org.kia.marshaler.impl

import org.kia.exception.ArgsException
import org.kia.exception.code.ErrorCode
import org.kia.marshaler.AbstractArgumentMarshaler

class StringArgumentMarshaler(override var value: String = "") : AbstractArgumentMarshaler<String>() {
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