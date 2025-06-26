package org.kia.exception

import org.kia.exception.code.ErrorCode

class ErrorTracker {
    private val errorArguments = mutableMapOf<Char, ErrorCode>()
    private val unexpectedArguments = sortedSetOf<Char>()
    private val unexpectedSchemaElements = sortedSetOf<String>()
    private var valid: Boolean = true

    fun addUnexpectedArgument(arg: Char) {
        unexpectedArguments.add(arg)
        makeInvalid()
    }

    fun addErrorArgument(arg: Char, code: ErrorCode) {
        errorArguments[arg] = code
        makeInvalid()
    }

    fun addUnexpectedSchemaElement(element: String) {
        unexpectedSchemaElements.add(element)
        makeInvalid()
    }

    private fun makeInvalid() {
        if (valid) {
            valid = false
        }
    }

    fun noProblems() = valid

    fun describe(): String {
        val unexpectedArgumentDescription = getUnexpectedArgumentDescription()
        val errorArgumentsDescription = getErrorArgumentsDescription()
        val unexpectedSchemaElementsDescription = getUnexpectedSchemaElementsDescription()
        return concatenateStrings(
            unexpectedArgumentDescription,
            errorArgumentsDescription,
            unexpectedSchemaElementsDescription
        )
    }

    private fun buildErrorMessage(errorArgument: Char, errorCode: ErrorCode) = when (errorCode) {
        ErrorCode.MISSING_STRING -> "Could not find string parameter for -$errorArgument."
        ErrorCode.MISSING_INT -> "Could not find int parameter for -$errorArgument."
        ErrorCode.INVALID_INT -> "Invalid int parameter for -$errorArgument."
        ErrorCode.MISSING_DOUBLE -> "Could not find double parameter for -$errorArgument."
        ErrorCode.INVALID_DOUBLE -> "Invalid double parameter for -$errorArgument."
        ErrorCode.OK -> throw Exception("TILT: Should not get here.")
    }

    private fun getUnexpectedArgumentDescription(): String? {
        if (unexpectedArguments.isEmpty()) {
            return null
        }
        val unexpectedArgumentsString = unexpectedArguments.joinToString(",")
        return "Argument(s) $unexpectedArgumentsString unexpected."
    }

    private fun getUnexpectedSchemaElementsDescription(): String? {
        if (unexpectedSchemaElements.isEmpty()) {
            return null
        }
        val unexpectedSchemaElementsString = unexpectedSchemaElements.joinToString(",")
        return "Schema element(s) $unexpectedSchemaElementsString unexpected."
    }

    private fun getErrorArgumentsDescription(): String? {
        if (errorArguments.isEmpty()) {
            return null
        }
        return errorArguments.map { (argument, code) -> buildErrorMessage(argument, code) }.joinToString("\n")
    }

    private fun concatenateStrings(vararg values: String?): String {
        return listOfNotNull(*values).joinToString(separator = "\n")
    }
}