package com.solanteq.solar.backoffice.parser

import com.solanteq.solar.backoffice.exception.ParseException
import com.solanteq.solar.backoffice.marshaller.ArgumentMarshaler
import com.solanteq.solar.backoffice.marshaller.BooleanArgumentMarshaler
import com.solanteq.solar.backoffice.marshaller.IntArgumentMarshaler
import com.solanteq.solar.backoffice.marshaller.StringArgumentMarshaler

class SchemaParser(
    val schema: String
) {
    private val marshallers = mutableMapOf<Char, ArgumentMarshaler<*>>()

    fun parse(): MutableMap<Char, ArgumentMarshaler<*>> {
        if (schema.isEmpty() || marshallers.isNotEmpty()) {
            return marshallers
        }
        parseSchema()
        return marshallers
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
        val elementId = getElementId(element)
        val marker = getElementMarker(element)
        val marshaler = getMarshaler(marker)
        marshallers[elementId] = marshaler
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
        else -> BooleanArgumentMarshaler()
    }

    companion object {
        private const val DELIMITER = ','
        private const val STRING_MARKER = "*"
        private const val INT_MARKER = "#"
    }
}