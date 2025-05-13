package com.solanteq.solar.backoffice

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @since %CURRENT_VERSION%
 */
class ArgsTest {
    @Test
    fun parseBoolean() {
        val schema = "a,b,d"
        val args = Array(3) { "" }
        args[0] = "-a"
        args[1] = "-c"
        args[2] = "-b"
        val argsParser = Args(schema, args)

        println("is valid: ${argsParser.isValid()}")
        assertFalse(argsParser.isValid())

        println("cardinality: ${argsParser.cardinality()}")
        assertEquals(2, argsParser.cardinality())

        println("usage: ${argsParser.usage()}")
        assertEquals("-[a,b,d]", argsParser.usage())

        println("error message: ${argsParser.errorMessage()}")
        assertEquals("Argument(s) - c unexpected.", argsParser.errorMessage())

        println("boolean value for a: ${argsParser.getBoolean('a')}")
        assertTrue(argsParser.getBoolean('a'))

        println("boolean value for b: ${argsParser.getBoolean('b')}")
        assertTrue(argsParser.getBoolean('b'))

        println("boolean value for c: ${argsParser.getBoolean('c')}")
        assertFalse(argsParser.getBoolean('c'))

        println("boolean value for d: ${argsParser.getBoolean('d')}")
        assertFalse(argsParser.getBoolean('d'))
    }

    @Test
    fun parseString() {
        val schema = "a*,b*,d*"
        val args = Array(6) { "" }
        args[0] = "-a"
        args[1] = "test1"
        args[2] = "-c"
        args[3] = "test3"
        args[4] = "-b"
        args[5] = "test2"
        val argsParser = Args(schema, args)

        println("is valid: ${argsParser.isValid()}")
        assertFalse(argsParser.isValid())

        println("cardinality: ${argsParser.cardinality()}")
        assertEquals(2, argsParser.cardinality())

        println("usage: ${argsParser.usage()}")
        assertEquals("-[a*,b*,d*]", argsParser.usage())

        println("error message: ${argsParser.errorMessage()}")
        assertEquals("Argument(s) - c unexpected.", argsParser.errorMessage())

        println("string value for a: ${argsParser.getString('a')}")
        assertEquals("test1", argsParser.getString('a'))

        println("string value for b: ${argsParser.getString('b')}")
        assertEquals("test2", argsParser.getString('b'))

        println("string value for c: ${argsParser.getString('c')}")
        assertTrue(argsParser.getString('c').isEmpty())

        println("string value for d: ${argsParser.getString('d')}")
        assertTrue(argsParser.getString('d').isEmpty())
    }

    @Test
    fun parseInt() {
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
        val argsParser = Args(schema, args)

        println("is valid: ${argsParser.isValid()}")
        assertFalse(argsParser.isValid())

        println("cardinality: ${argsParser.cardinality()}")
        assertEquals(3, argsParser.cardinality())

        println("usage: ${argsParser.usage()}")
        assertEquals("-[a#,b#,d#,f#]", argsParser.usage())

        println("error message: ${argsParser.errorMessage()}")
        assertEquals("Argument(s) - c unexpected.", argsParser.errorMessage())

        println("int value for a: ${argsParser.getInt('a')}")
        assertEquals(1, argsParser.getInt('a'))

        println("int value for b: ${argsParser.getInt('b')}")
        assertEquals(-1, argsParser.getInt('b'))

        println("int value for c: ${argsParser.getInt('c')}")
        assertEquals(0, argsParser.getInt('c'))

        println("int value for d: ${argsParser.getInt('d')}")
        assertEquals(0, argsParser.getInt('d'))

        println("int value for f: ${argsParser.getInt('f')}")
        assertEquals(0, argsParser.getInt('f'))
    }
}