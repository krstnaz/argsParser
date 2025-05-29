package com.solanteq.solar.backoffice.exception

/**
 * @since %CURRENT_VERSION%
 */
class ErrorTracker {
    private val errorArguments = mutableMapOf<Char, ErrorCode>()
    private val unexpectedArguments = sortedSetOf<Char>()
    private var valid: Boolean = true

    fun addUnexpectedArgument(arg: Char) {
        unexpectedArguments.add(arg)
        makeInvalid()
    }

    fun addErrorArgument(arg: Char, code: ErrorCode) {
        errorArguments[arg] = code
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
        return concatenateStrings(unexpectedArgumentDescription, errorArgumentsDescription)
    }

    private fun buildErrorMessage(errorArgument: Char, errorCode: ErrorCode) = when (errorCode) {
        ErrorCode.MISSING_STRING -> "Could not find string parameter for -$errorArgument."
        ErrorCode.MISSING_INT -> "Could not find int parameter for -$errorArgument."
        ErrorCode.INVALID_INT -> "Invalid int parameter for -$errorArgument."
        ErrorCode.OK -> throw Exception("TILT: Should not get here.")
    }

    private fun getUnexpectedArgumentDescription(): String? {
        if (unexpectedArguments.isEmpty()) {
            return null
        }
        val unexpectedArgumentsString = unexpectedArguments.joinToString(",")
        return "Argument(s) - $unexpectedArgumentsString unexpected."
    }

    private fun getErrorArgumentsDescription(): String? {
        if (errorArguments.isEmpty()) {
            return null
        }
        return errorArguments.map { (argument, code) -> buildErrorMessage(argument, code) }.joinToString("\n")
    }

    fun concatenateStrings(str1: String?, str2: String?): String {
        return listOfNotNull(str1, str2).joinToString(separator = "\n")
    }
}