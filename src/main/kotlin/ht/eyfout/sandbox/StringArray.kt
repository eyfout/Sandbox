package ht.eyfout.sandbox

import java.lang.IndexOutOfBoundsException


/**
 *  A String[] implementation that store an array of Strings as char[].
 *  The position of the string is calculated using an address array, which maintains
 *  the starting position of the String index and its length.
 */
interface StringArray {
    operator fun set(index: Int, value: String)
    operator fun get(index: Int): String
    fun size() : Int
}


class StringCharArray : StringArray {
    lateinit var values: CharArray
    lateinit var address: IntArray
    private var size = 0


    /**
     * Returns the String value at the specified index
     * @throws IndexOutOfBoundsException
     */
    override operator fun get(index: Int) = if (this::address.isInitialized && address.size >= (index * 2 + 1)) {
        String(values, address[index * 2], address[index * 2 + 1])
    } else {
        throw IndexOutOfBoundsException()
    }

    /**
     * Dynamically sets the value in the String array.
     * It will expand to fit any size array
     */
    override operator fun set(index: Int, value: String) {
        if (this::values.isInitialized) {
            shiftValues(index, value)
        } else {
            val indexInAddressArr = index * 2
            values = value.toCharArray()
            address = IntArray(indexInAddressArr + 2) { -1 }
            address[indexInAddressArr] = 0
            address[indexInAddressArr + 1] = value.length
        }
    }

    private fun shiftValues(index: Int, value: String) {
        val indexInAddressArr = index * 2
        if (address.size <= indexInAddressArr) {
            address = address.copyOf(indexInAddressArr + 2)
            addValue(index, value)
        } else {
            if (address[indexInAddressArr + 1] <= 0) {
                addValue(index, value)
            } else {
                val position = address[indexInAddressArr]

                val delta = value.length - address[indexInAddressArr + 1]

                if (delta > 0) {
                    val tmpValues = values.copyOf(values.size + delta)
                    value.toCharArray().copyInto(tmpValues, position)
                    values.copyInto(tmpValues, position + value.length, position + address[indexInAddressArr + 1])
                    for (n in address.indices step 2) {
                        if (address[n] > position) {
                            address[n] += delta
                        }
                    }
                    values = tmpValues
                } else {
                    value.toCharArray().copyInto(values, position)
                }
                address[indexInAddressArr + 1] = value.length
            }
        }
    }

    private fun addValue(index: Int, value: String) {
        size++
        address[index * 2] = values.size
        address[index * 2 + 1] = value.length
        values = values.copyOf(values.size + value.length)
        value.toCharArray().copyInto(values, address[index * 2])
    }
    override fun size() = size
}


class CompressedStringArray : StringArray {
    lateinit var values: CharArray
    lateinit var address: IntArray
    private var size = 0

    private fun index(i: Int) = i shr 16
    private fun length(i: Int) = 0x00FF and i


    override operator fun get(index: Int) = if (this::address.isInitialized && address.size >= index) {
        String(values, index(address[index]), length(address[index]))
    } else {
        throw IndexOutOfBoundsException()
    }

    override operator fun set(index: Int, value: String) {
        if (this::values.isInitialized) {
            shiftValues(index, value)
        } else {
            values = value.toCharArray()
            address = IntArray(index + 2) { 0 }
            address[index] = addressCalc(0, value.length)
        }
    }

    private fun addressCalc(position: Int, length: Int) = position shl 16 or length

    private fun shiftValues(index: Int, value: String) {
        if (address.size <= index) {
            address = address.copyOf(index + 2)
            addValue(index, value)
        } else {
            if (length(address[index]) <= 0) {
                addValue(index, value)
            } else {
                val position = index(address[index])
                val delta = value.length - length(address[index])

                if (delta > 0) {
                    val tmpValues = values.copyOf(values.size + delta)
                    value.toCharArray().copyInto(tmpValues, position)
                    values.copyInto(tmpValues, position + value.length, position + length(address[index]))
                    for (n in address.indices) {
                        val i = index(address[n])
                        val l = length(address[n])
                        if (i > position) {
                            address[n] = addressCalc(i + delta, l)
                        }
                    }
                    values = tmpValues
                } else {
                    value.toCharArray().copyInto(values, position)
                }
                address[index] = addressCalc(index(address[index]), value.length)
            }
        }
    }

    private fun addValue(index: Int, value: String) {
        size++
        address[index] = addressCalc(values.size, value.length)
        values = values.copyOf(values.size + value.length)
        value.toCharArray().copyInto(values, index(address[index]))
    }


    override fun size() = size
}