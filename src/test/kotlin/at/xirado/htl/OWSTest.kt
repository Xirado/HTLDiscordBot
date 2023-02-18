package at.xirado.htl

import at.xirado.htl.utils.merge
import at.xirado.htl.utils.parseOWSMessage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

fun mergeMessages(vararg individual: String, log: Boolean = false): String {
    return individual.map(::parseOWSMessage)
        .also { if (log) println(it.joinToString("\n")) }
        .merge()
}

class OWSTest {
    @Test
    fun testMergedWords1() {
        val merged = mergeMessages("Hi", "Hello", "-World", "Hi")
        assertEquals("Hi HelloWorld Hi", merged)
    }

    @Test
    fun testMergedWords2() {
        val merged = mergeMessages("Hi", "Hello-", "World", "Hi")
        assertEquals("Hi HelloWorld Hi", merged)
    }

    @Test
    fun testMergedWords3() {
        val merged = mergeMessages("Hi", "-Hello-", "World", "Hi")
        assertEquals("HiHelloWorld Hi", merged)
    }

    @Test
    fun testPunctuation1() {
        val merged = mergeMessages("Hi", "Hello,", "World", "Hi")
        assertEquals("Hi Hello, World Hi", merged)
    }

    @Test
    fun testPunctuation2() {
        val merged = mergeMessages("Hi", "Hello", ", World", "Hi")
        assertEquals("Hi Hello, World Hi", merged)
    }

    @Test
    fun testPunctuation3() {
        val merged = mergeMessages("Hi", "Hello", ". World", "Hi")
        assertEquals("Hi Hello. World Hi", merged)
    }
}