package org.kia.parser

import org.kia.exception.ArgsException
import org.kia.exception.ErrorTracker
import org.kia.marshaler.AbstractArgumentMarshaler

class ArgumentParser(
    private val args: List<String>
) {
    private lateinit var currentArgument: Iterator<String>
    private lateinit var marshalers: MutableMap<Char, AbstractArgumentMarshaler<*>>
    val errorTracker: ErrorTracker = ErrorTracker()
    val argsFound: MutableSet<Char> = mutableSetOf()

    fun parse(
        marshalers: MutableMap<Char, AbstractArgumentMarshaler<*>>
    ): MutableMap<Char, AbstractArgumentMarshaler<*>> {
        if (args.isEmpty() && marshalers.isEmpty()) {
            return mutableMapOf()
        }
        this.marshalers = marshalers
        currentArgument = args.iterator()
        while (currentArgument.hasNext()) {
            val arg = currentArgument.next()
            parseArgument(arg)
        }
        return this.marshalers
    }

    private fun parseArgument(arg: String) {
        if (arg.startsWith("-") && arg.toIntOrNull() == null) {
            parseElements(arg)
        }
    }

    private fun parseElements(arg: String) {
        for (i in 1..<arg.length) {
            if (marshalers.containsKey(arg[i])) {
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
        val marshaler = marshalers[argChar]
        try {
            return marshaler?.set(currentArgument) ?: false
        } catch (e: ArgsException) {
            errorTracker.addErrorArgument(argChar, e.errorCode)
            return false
        }
    }
}