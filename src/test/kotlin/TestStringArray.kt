import ht.eyfout.oracle.HeapDumper
import ht.eyfout.sandbox.CompressedStringArray
import ht.eyfout.sandbox.StringCharArray
import org.junit.Assert
import org.junit.Test

class TestStringArray {

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
        HeapDumper.main(arrayOf("string-array.hprof", "true"))
    }
}