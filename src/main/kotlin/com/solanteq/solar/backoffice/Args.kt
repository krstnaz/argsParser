package com.solanteq.solar.backoffice

import com.solanteq.solar.backoffice.exception.ArgsException
import com.solanteq.solar.backoffice.exception.ErrorTracker
import com.solanteq.solar.backoffice.exception.ParseException
import com.solanteq.solar.backoffice.marshaller.ArgumentMarshaller
import com.solanteq.solar.backoffice.marshaller.BooleanArgumentMarshaller
import com.solanteq.solar.backoffice.marshaller.IntArgumentMarshaller
import com.solanteq.solar.backoffice.marshaller.StringArgumentMarshaller

/**
 * @since %CURRENT_VERSION%
 */
class Args {
    private val schema: String
    private val args: List<String>
    private val marshallers = mutableMapOf<Char, ArgumentMarshaller<*>>()
    private val argsFound = mutableSetOf<Char>()
    private val errorTracker: ErrorTracker = ErrorTracker()
    private lateinit var currentArgument: Iterator<String>

    constructor(schema: String, args: Array<String>) {
        this.schema = schema
        this.args = listOf(*args)
    }

    fun parse() {
        if (schema.isEmpty() && args.isEmpty()) {
            return
        }
        parseSchema()
        parseArguments()
    }

    private fun parseSchema() {
        for (element in schema.split(",")) {
            if (element.isNotEmpty()) {
                val trimmedElement = element.trim()
                parseSchemaElement(trimmedElement)
            }
        }
    }

    private fun parseSchemaElement(element: String) {
        val elementId = element[0]
        val elementTail = element.substring(1)
        validateSchemaElementId(elementId)
        if (isBooleanSchemaElement(elementTail)) {
            parseBooleanSchemaElement(elementId)
        } else if (isStringSchemaElement(elementTail)) {
            parseStringSchemaElement(elementId)
        } else if (isIntSchemaElement(elementTail)) {
            parseIntSchemaElement(elementId)
        }
    }

    private fun validateSchemaElementId(elementId: Char) {
        if (!elementId.isLetter()) {
            throw ParseException("Bad character: $elementId in Args format: $schema")
        }
    }

    private fun parseStringSchemaElement(elementId: Char) {
        marshallers[elementId] = StringArgumentMarshaller()
    }

    private fun isStringSchemaElement(elementTail: String): Boolean {
        return elementTail == "*"
    }

    private fun isBooleanSchemaElement(elementTail: String): Boolean {
        return elementTail.isEmpty()
    }

    private fun parseBooleanSchemaElement(elementId: Char) {
        marshallers[elementId] = BooleanArgumentMarshaller()
    }

    private fun isIntSchemaElement(elementTail: String): Boolean {
        return elementTail == "#"
    }

    private fun parseIntSchemaElement(elementId: Char) {
        marshallers[elementId] = IntArgumentMarshaller()
    }

    private fun parseArguments(): Boolean {
        currentArgument = args.iterator()
        while (currentArgument.hasNext()) {
            val arg = currentArgument.next()
            parseArgument(arg)
        }
        return true
    }

    private fun parseArgument(arg: String) {
        if (arg.startsWith("-") && arg.toIntOrNull() == null) {
            parseElements(arg)
        }
    }

    private fun parseElements(arg: String) {
        for (i in 1..<arg.length) {
            if (marshallers.containsKey(arg[i])) {
                parseElement(arg[i])
            } else {
                errorTracker.addUnexpectedArgument(arg[i])
            }
        }
    }

    private fun parseElement(argChar: Char) {
        if (setArgument(argChar)) {
            argsFound.add(argChar)
        }
    }

    private fun setArgument(argChar: Char): Boolean {
        val marshaller = marshallers[argChar]
        try {
            return marshaller?.set(currentArgument) ?: false
        } catch (e: ArgsException) {
            errorTracker.addErrorArgument(argChar, e.errorCode)
            return false
        }
    }

    fun cardinality(): Int {
        return argsFound.size
    }

    fun usage(): String {
        return if (schema.isNotEmpty()) {
            "-[$schema]"
        } else {
            ""
        }
    }

    fun errorMessages() = errorTracker.describe()

    fun get(arg: Char) = marshallers[arg]?.get()

//    fun getBoolean(arg: Char): Boolean {
//        val am = marshallers[arg]
//        return am != null && am.get() as? Boolean ?: false
//    }

//    fun getString(arg: Char): String {
//        val am = marshallers[arg]
//        return am?.get() as? String ?: ""
//    }

//    fun getInt(arg: Char): Int {
//        val am = marshallers[arg]
//        return am?.get() as? Int ?: 0
//    }

    fun isValid() = errorTracker.hasProblems()
}