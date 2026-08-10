package com.thebipolaroptimist.stuffrandomizer.utilties

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class QuickSelectTest {

    @Test
    fun pickRandom_emptyList_returnsNull() {
        assertNull(QuickSelect.pickRandom(listOf()))
    }

    @Test
    fun pickRandom_onlyBlankEntries_returnsNull() {
        assertNull(QuickSelect.pickRandom(listOf("", "")))
    }

    @Test
    fun pickRandom_singleItem_returnsThatItem() {
        assertEquals("Item1", QuickSelect.pickRandom(listOf("Item1")))
    }

    @Test
    fun pickRandom_ignoresBlankEntries() {
        val result = QuickSelect.pickRandom(listOf("", "Item1", "", "Item2"))
        assertTrue(result == "Item1" || result == "Item2")
    }
}
