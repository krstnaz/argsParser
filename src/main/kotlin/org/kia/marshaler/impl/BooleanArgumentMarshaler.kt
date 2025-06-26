package org.kia.marshaler.impl

import org.kia.marshaler.AbstractArgumentMarshaler

class BooleanArgumentMarshaler(override var value: Boolean = false) : AbstractArgumentMarshaler<Boolean>() {
    override fun set(currentArgument: Iterator<String>?): Boolean {
        this.value = true
        return true
    }
}