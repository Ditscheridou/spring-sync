package de.jds.diffi.algorithms

import de.jds.diffi.types.diffAware
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HeckelTest {

    @Test
    fun testDiff() {
        val changes = Heckel<de.jds.diffi.types.String>().diff(
            mutableListOf("ab".diffAware()),
            mutableListOf("".diffAware())
        )

        assertEquals(2, changes.size)
    }

}