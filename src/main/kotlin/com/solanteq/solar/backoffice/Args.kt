package com.solanteq.solar.backoffice

import com.solanteq.solar.backoffice.marshaller.ArgumentMarshaller
import com.solanteq.solar.backoffice.marshaller.BooleanArgumentMarshaller
import com.solanteq.solar.backoffice.marshaller.IntArgumentMarshaller
import com.solanteq.solar.backoffice.marshaller.StringArgumentMarshaller

/**
 * @since %CURRENT_VERSION%
 */
class Args {
    private val schema: String
    private val args: Array<String>
    private var valid: Boolean
    private val unexpectedArguments = sortedSetOf<Char>()
    private val booleanArgs = mutableMapOf<Char, ArgumentMarshaller<Boolean>>()
    private val stringArgs = mutableMapOf<Char, ArgumentMarshaller<String>>()
    private val intArgs = mutableMapOf<Char, ArgumentMarshaller<Int>>()
    private val argsFound = mutableSetOf<Char>()
    private var currentArgument = 0
    private var errorArgument = '0'

    enum class ErrorCode {
        OK, MISSING_STRING, MISSING_INT, INVALID_INT
    }

    class ParseException(message: String) : Exception(message)

    private var errorCode = ErrorCode.OK

    constructor(schema: String, args: Array<String>) {
        this.schema = schema
        this.args = args
        this.valid = parse()
    }

    private fun parse(): Boolean {
        if (schema.isEmpty() && args.isEmpty()) {
            return true
        }
        parseSchema()
        parseArguments()
        return valid
    }

    private fun parseSchema(): Boolean {
        for (element in schema.split(",")) {
            if (element.isNotEmpty()) {
                val trimmedElement = element.trim()
                parseSchemaElement(trimmedElement)
            }
        }
        return true
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
        stringArgs[elementId] = StringArgumentMarshaller()
    }

    private fun isStringSchemaElement(elementTail: String): Boolean {
        return elementTail == "*"
    }

    private fun isBooleanSchemaElement(elementTail: String): Boolean {
        return elementTail.isEmpty()
    }

    private fun parseBooleanSchemaElement(elementId: Char) {
        booleanArgs[elementId] = BooleanArgumentMarshaller()
    }

    private fun isIntSchemaElement(elementTail: String): Boolean {
        return elementTail == "#"
    }

    private fun parseIntSchemaElement(elementId: Char) {
        intArgs[elementId] = IntArgumentMarshaller()
    }

    private fun parseArguments(): Boolean {
        for (i in args.indices) {
            currentArgument = i
            parseArgument(args[i])
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
            parseElement(arg[i])
        }
    }

    private fun parseElement(argChar: Char) {
        if (setArgument(argChar)) {
            argsFound.add(argChar)
        } else {
            unexpectedArguments.add(argChar)
            valid = false
        }
    }

    private fun setArgument(argChar: Char): Boolean {
        var set = true
        if (isBoolean(argChar)) {
            setBooleanArg(argChar, true)
        } else if (isString(argChar)) {
            setStringArg(argChar, "")
        } else if (isInt(argChar)) {
            setIntArg(argChar, 0)
        } else {
            set = false
        }
        return set
    }

    private fun isString(argChar: Char): Boolean {
        return stringArgs.containsKey(argChar)
    }

    private fun setStringArg(argChar: Char, s: String) {
        currentArgument++
        try {
            stringArgs[argChar]?.set(args[currentArgument])
        } catch (e: ArrayIndexOutOfBoundsException) {
            valid = false
            errorArgument = argChar
            errorCode = ErrorCode.MISSING_STRING
        }
    }

    private fun isBoolean(argChar: Char): Boolean {
        return booleanArgs.containsKey(argChar)
    }

    private fun setBooleanArg(argChar: Char, value: Boolean) {
        booleanArgs[argChar]?.set(value)
    }

    private fun isInt(argChar: Char): Boolean {
        return intArgs.containsKey(argChar)
    }

    private fun setIntArg(argChar: Char, s: Int) {
        currentArgument++
        try {
            intArgs[argChar]?.set(Integer.parseInt(args[currentArgument]))
        } catch (e: ArrayIndexOutOfBoundsException) {
            valid = false
            errorArgument = argChar
            errorCode = ErrorCode.MISSING_INT
        } catch (e: NumberFormatException) {
            valid = false
            errorArgument = argChar
            errorCode = ErrorCode.INVALID_INT
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

    fun errorMessage(): String {
        return if (unexpectedArguments.isNotEmpty()) {
            unexpectedArgumentMessage()
        } else {
            when (errorCode) {
                ErrorCode.MISSING_STRING -> "Could not find string parameter for -$errorArgument."
                ErrorCode.MISSING_INT -> "Could not find int parameter for -$errorArgument."
                ErrorCode.INVALID_INT -> "Invalid int parameter for -$errorArgument."
                ErrorCode.OK -> throw Exception("TILT: Should not get here.")
            }
        }
    }

    private fun unexpectedArgumentMessage(): String {
        val message = StringBuilder("Argument(s) - ")
        for (c in unexpectedArguments) {
            message.append(c.toString())
        }
        message.append(" unexpected.")
        return message.toString()
    }

    fun getBoolean(arg: Char): Boolean {
        val am = booleanArgs[arg]
        return am != null && am.get()
    }

    fun getString(arg: Char): String {
        val am = stringArgs[arg]
        return am?.get() ?: ""
    }

    fun getInt(arg: Char): Int {
        val am = intArgs[arg]
        return am?.get() ?: 0
    }

    fun isValid() = valid
}