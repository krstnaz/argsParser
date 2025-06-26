package org.kia.parser

import org.kia.exception.ErrorTracker
import org.kia.exception.ParseException
import org.kia.marshaler.AbstractArgumentMarshaler
import org.kia.marshaler.impl.BooleanArgumentMarshaler
import org.kia.marshaler.impl.DoubleArgumentMarshaler
import org.kia.marshaler.impl.IntArgumentMarshaler
import org.kia.marshaler.impl.StringArgumentMarshaler

class SchemaParser(
    val schema: String
) {
    private val marshalers = mutableMapOf<Char, AbstractArgumentMarshaler<*>>()
    val errorTracker: ErrorTracker = ErrorTracker()

    fun parse(): MutableMap<Char, AbstractArgumentMarshaler<*>> {
        if (schema.isEmpty() || marshalers.isNotEmpty()) {
            return marshalers
        }
        parseSchema()
        return marshalers
    }

    private fun parseSchema() {
        for (element in schema.split(DELIMITER)) {
            val trimmedElement = element.trim()
            if (trimmedElement.isNotEmpty()) {
                parseSchemaElement(trimmedElement)
            }
        }
    }

    private fun parseSchemaElement(element: String) {
        try {
            val elementId = getElementId(element)
            val marker = getElementMarker(element)
            val marshaler = getMarshaler(marker)
            marshalers[elementId] = marshaler
        } catch (e: ParseException) {
            errorTracker.addUnexpectedSchemaElement(element)
        }
    }

    private fun getElementId(element: String): Char {
        val elementId = element[0]
        validateSchemaElementId(elementId)
        return elementId
    }

    private fun getElementMarker(element: String) = element.substring(1)

    private fun validateSchemaElementId(elementId: Char) {
        if (!elementId.isLetter()) {
            throw ParseException("Bad character: $elementId in schema: $schema")
        }
    }

    private fun getMarshaler(marker: String) = when (marker) {
        STRING_MARKER -> StringArgumentMarshaler()
        INT_MARKER -> IntArgumentMarshaler()
        BOOLEAN_MARKER -> BooleanArgumentMarshaler()
        DOUBLE_MARKER -> DoubleArgumentMarshaler()
        else -> throw ParseException("Unknown element marker")
    }

    companion object {
        private const val DELIMITER = ','
        private const val STRING_MARKER = "*"
        private const val INT_MARKER = "#"
        private const val BOOLEAN_MARKER = ""
        private const val DOUBLE_MARKER = "##"
    }
}