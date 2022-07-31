package com.jackemate.appberdi.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun `1 millisecond equal to 0`() {
        val out = 1.toTimeString()
        assertEquals(out, "00:00")
    }

    @Test
    fun `-1 millisecond equal to 0`() {
        val out = (-1).toTimeString()
        println(out)
        assertEquals(out, "00:00")
    }

    @Test
    fun `negative millisecond equal to negative`() {
        val out = (-1000).toTimeString()
        assertEquals(out, "00:-1")
    }

}