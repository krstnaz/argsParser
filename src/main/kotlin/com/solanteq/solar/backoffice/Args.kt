package com.solanteq.solar.backoffice

import com.solanteq.solar.backoffice.marshaller.ArgumentMarshaler
import com.solanteq.solar.backoffice.parser.ArgumentParser
import com.solanteq.solar.backoffice.parser.SchemaParser

class Args(
    private val schemaParser: SchemaParser,
    private val argumentParser: ArgumentParser
) {
    private val marshalers = mutableMapOf<Char, ArgumentMarshaler<*>>()

    fun parse() {
        val emptyMarshallers = schemaParser.parse()
        val filledMarshallers = argumentParser.parse(emptyMarshallers)
        marshalers.putAll(filledMarshallers)
    }

    fun cardinality() = argumentParser.argsFound.size

    fun usage(): String {
        val schema = schemaParser.schema
        return if (schema.isNotEmpty()) {
            "-[$schema]"
        } else {
            ""
        }
    }

    fun errorMessages() = argumentParser.errorTracker.describe()

    fun get(arg: Char) = marshalers[arg]?.get()

    fun isValid() = argumentParser.errorTracker.noProblems()
}