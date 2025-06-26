package org.kia

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.kia.parser.ArgumentParser
import org.kia.parser.SchemaParser
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArgsTest {
    @Test
    fun `parse - boolean args with incorrect value - correct results`() {
        val schema = "a,b,d"
        val args = Array(3) { "" }
        args[0] = "-a"
        args[1] = "-c"
        args[2] = "-b"
        val schemaParser = SchemaParser(schema)
        val argumentParser = ArgumentParser(listOf(*args))
        val argsParser = Args(schemaParser, argumentParser)
        argsParser.parse()

        println("is valid: ${argsParser.isValid()}")
        assertFalse(argsParser.isValid())

        println("cardinality: ${argsParser.cardinality()}")
        assertEquals(2, argsParser.cardinality())

        println("usage: ${argsParser.usage()}")
        assertEquals("-[a,b,d]", argsParser.usage())

        println("error message: ${argsParser.errorMessages()}")
        assertEquals("Argument(s) c unexpected.", argsParser.errorMessages())

        println("boolean value for a: ${argsParser.get('a')}")
        assertTrue(argsParser.get('a') as Boolean)

        println("boolean value for b: ${argsParser.get('b')}")
        assertTrue(argsParser.get('b') as Boolean)

        println("boolean value for c: ${argsParser.get('c')}")
        assertNull(argsParser.get('c'))

        println("boolean value for d: ${argsParser.get('d')}")
        assertFalse(argsParser.get('d') as Boolean)
    }

    @Test
    fun `parse - string args with incorrect value - correct results`() {
        val schema = "a*,b*,d*"
        val args = Array(6) { "" }
        args[0] = "-a"
        args[1] = "test1"
        args[2] = "-c"
        args[3] = "test3"
        args[4] = "-b"
        args[5] = "test2"
        val schemaParser = SchemaParser(schema)
        val argumentParser = ArgumentParser(listOf(*args))
        val argsParser = Args(schemaParser, argumentParser)
        argsParser.parse()

        println("is valid: ${argsParser.isValid()}")
        assertFalse(argsParser.isValid())

        println("cardinality: ${argsParser.cardinality()}")
        assertEquals(2, argsParser.cardinality())

        println("usage: ${argsParser.usage()}")
        assertEquals("-[a*,b*,d*]", argsParser.usage())

        println("error message: ${argsParser.errorMessages()}")
        assertEquals("Argument(s) c unexpected.", argsParser.errorMessages())

        println("string value for a: ${argsParser.get('a')}")
        assertEquals("test1", argsParser.get('a'))

        println("string value for b: ${argsParser.get('b')}")
        assertEquals("test2", argsParser.get('b'))

        println("string value for c: ${argsParser.get('c')}")
        assertNull(argsParser.get('c'))

        println("string value for d: ${argsParser.get('d')}")
        assertTrue((argsParser.get('d') as String).isEmpty())
    }

    @Test
    fun `parse - int args with incorrect value - correct results`() {
        val schema = "a#,b#,d#,f#"
        val args = Array(8) { "" }
        args[0] = "-a"
        args[1] = "1"
        args[2] = "-c"
        args[3] = "0"
        args[4] = "-b"
        args[5] = "-1"
        args[6] = "-f"
        args[7] = "notInt"
        val schemaParser = SchemaParser(schema)
        val argumentParser = ArgumentParser(listOf(*args))
        val argsParser = Args(schemaParser, argumentParser)
        argsParser.parse()

        println("is valid: ${argsParser.isValid()}")
        assertFalse(argsParser.isValid())

        println("cardinality: ${argsParser.cardinality()}")
        assertEquals(2, argsParser.cardinality())

        println("usage: ${argsParser.usage()}")
        assertEquals("-[a#,b#,d#,f#]", argsParser.usage())

        println("error message: ${argsParser.errorMessages()}")
        assertEquals("Argument(s) c unexpected.\nInvalid int parameter for -f.", argsParser.errorMessages())

        println("int value for a: ${argsParser.get('a')}")
        assertEquals(1, argsParser.get('a'))

        println("int value for b: ${argsParser.get('b')}")
        assertEquals(-1, argsParser.get('b'))

        println("int value for c: ${argsParser.get('c')}")
        assertNull(argsParser.get('c'))

        println("int value for d: ${argsParser.get('d')}")
        assertEquals(0, argsParser.get('d'))

        println("int value for f: ${argsParser.get('f')}")
        assertEquals(0, argsParser.get('f'))
    }

    @Test
    fun `parse - different type args - correct results`() {
        val schema = "a,b*,c#"
        val args = Array(5) { "" }
        args[0] = "-a"
        args[1] = "-b"
        args[2] = "someString"
        args[3] = "-c"
        args[4] = "7"
        val schemaParser = SchemaParser(schema)
        val argumentParser = ArgumentParser(listOf(*args))
        val argsParser = Args(schemaParser, argumentParser)
        argsParser.parse()

        println("is valid: ${argsParser.isValid()}")
        assertTrue(argsParser.isValid())

        println("cardinality: ${argsParser.cardinality()}")
        assertEquals(3, argsParser.cardinality())

        println("usage: ${argsParser.usage()}")
        assertEquals("-[a,b*,c#]", argsParser.usage())

        println("boolean value for a: ${argsParser.get('a')}")
        assertTrue(argsParser.get('a') as Boolean)

        println("string value for b: ${argsParser.get('b')}")
        assertEquals("someString", argsParser.get('b'))

        println("int value for c: ${argsParser.get('c')}")
        assertEquals(7, argsParser.get('c'))
    }

    @Test
    fun `parse - when unknown schema`() {
        val schema = "a!"
        val args = Array(2) { "" }
        args[0] = "-a"
        args[1] = "test"
        val schemaParser = SchemaParser(schema)
        val argumentParser = ArgumentParser(listOf(*args))
        val argsParser = Args(schemaParser, argumentParser)
        argsParser.parse()

        println("is valid: ${argsParser.isValid()}")
        assertFalse(argsParser.isValid())

        println("cardinality: ${argsParser.cardinality()}")
        assertEquals(0, argsParser.cardinality())

        println("usage: ${argsParser.usage()}")
        assertEquals("-[a!]", argsParser.usage())

        println("any value for a: ${argsParser.get('a')}")
        assertNull(argsParser.get('a'))
    }
}