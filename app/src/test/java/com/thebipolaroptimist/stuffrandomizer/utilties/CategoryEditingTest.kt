package com.thebipolaroptimist.stuffrandomizer.utilties

import junit.framework.TestCase.assertEquals
import org.junit.Test

class CategoryEditingTest {

    @Test
    fun resolveBackAction_emptyNameAndThings_returnsDeleteEmpty() {
        assertEquals(
            CategoryEditing.SaveOutcome.DELETE_EMPTY,
            CategoryEditing.resolveBackAction("", listOf())
        )
    }

    @Test
    fun resolveBackAction_emptyThings_returnsWarnNoItems() {
        assertEquals(
            CategoryEditing.SaveOutcome.WARN_NO_ITEMS,
            CategoryEditing.resolveBackAction("Name", listOf())
        )
    }

    @Test
    fun resolveBackAction_emptyName_returnsWarnNoName() {
        assertEquals(
            CategoryEditing.SaveOutcome.WARN_NO_NAME,
            CategoryEditing.resolveBackAction("", listOf("Thing1"))
        )
    }

    @Test
    fun resolveBackAction_nameAndThingsPresent_returnsSave() {
        assertEquals(
            CategoryEditing.SaveOutcome.SAVE,
            CategoryEditing.resolveBackAction("Name", listOf("Thing1"))
        )
    }

    @Test
    fun sanitizeThings_removesBlankEntries() {
        assertEquals(
            listOf("Thing1", "Thing2"),
            CategoryEditing.sanitizeThings(listOf("Thing1", "", "Thing2", ""))
        )
    }
}
