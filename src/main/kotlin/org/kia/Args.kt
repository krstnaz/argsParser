package org.kia

import org.kia.marshaler.AbstractArgumentMarshaler
import org.kia.parser.ArgumentParser
import org.kia.parser.SchemaParser

class Args(
    private val schemaParser: SchemaParser,
    private val argumentParser: ArgumentParser
) {
    private val marshalers = mutableMapOf<Char, AbstractArgumentMarshaler<*>>()

    fun parse() {
        val emptyMarshalers = schemaParser.parse()
        val filledMarshalers = argumentParser.parse(emptyMarshalers)
        marshalers.putAll(filledMarshalers)
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

    fun errorMessages() = "${argumentParser.errorTracker.describe()}\n${schemaParser.errorTracker.describe()}".trim()

    fun get(arg: Char) = marshalers[arg]?.get()

    fun isValid() = argumentParser.errorTracker.noProblems() && schemaParser.errorTracker.noProblems()
}