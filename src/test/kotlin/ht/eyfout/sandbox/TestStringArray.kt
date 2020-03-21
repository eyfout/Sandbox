package ht.eyfout.sandbox

import ht.eyfout.oracle.HeapDumper
import org.junit.Assert
import org.junit.Test

class TestStringArray {
    val captureHeapDump = false

    private fun implemenations() = listOf(CompressedStringArray(), StringCharArray())
    @Test
    fun `can add to data structure`() {
        implemenations().forEach { store ->
            store[0] = "#0"
            Assert.assertEquals("#0", store[0])
        }
    }

    @Test
    fun `can add multiple items into data structure`() {
        implemenations().forEach { store ->
            store[0] = "#0"
            store[3] = "#3"
            Assert.assertEquals("#3", store[3])
            Assert.assertEquals("#0", store[0])
        }
    }

    @Test
    fun `can override a previously set value`() {
        implemenations().forEach { store ->
            store[0] = "#0"
            store[2] = "#2"
            store[1] = "#1"

            store[0] = "!#0"
            Assert.assertEquals("!#0", store[0])
            Assert.assertEquals("#1", store[1])
            Assert.assertEquals("#2", store[2])
        }
    }

    @Test
    fun `can override a previously set value in random order`() {
        implemenations().forEach { store ->
            store[2] = "#2"
            store[0] = "#0"
            store[1] = "#1"
            store[0] = "!#0"
            store[2] = "!#2"
            store[3] = "#3"

            Assert.assertEquals("!#0", store[0])
            Assert.assertEquals("#1", store[1])
            Assert.assertEquals("#3", store[3])
            Assert.assertEquals("!#2", store[2])
        }

    }

    @Test
    fun `maximum number of elements in array`() {
        implemenations().forEach { store ->
            for (x in 0..0x00FF) {
                store[x] = x.toString()
            }
            Assert.assertTrue(store.size() == 0x00FF)
            Assert.assertTrue(store[0x00FD] == 0x00FD.toString())
        }
        if (captureHeapDump) HeapDumper.main(arrayOf("string-array.hprof", "true"))
    }
}