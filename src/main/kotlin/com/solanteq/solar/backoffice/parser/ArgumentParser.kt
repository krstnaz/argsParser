package com.solanteq.solar.backoffice.parser

import com.solanteq.solar.backoffice.exception.ArgsException
import com.solanteq.solar.backoffice.exception.ErrorTracker
import com.solanteq.solar.backoffice.marshaller.ArgumentMarshaler

class ArgumentParser(
    private val args: List<String>,
    val errorTracker: ErrorTracker
) {
    private lateinit var currentArgument: Iterator<String>
    private lateinit var marshallers: MutableMap<Char, ArgumentMarshaler<*>>
    val argsFound: MutableSet<Char> = mutableSetOf()

    fun parse(marshallers: MutableMap<Char, ArgumentMarshaler<*>>): MutableMap<Char, ArgumentMarshaler<*>> {
        if (args.isEmpty() && marshallers.isEmpty()) {
            return mutableMapOf()
        }
        this.marshallers = marshallers
        currentArgument = args.iterator()
        while (currentArgument.hasNext()) {
            val arg = currentArgument.next()
            parseArgument(arg)
        }
        return this.marshallers
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
}