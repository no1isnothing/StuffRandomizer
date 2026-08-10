package com.thebipolaroptimist.stuffrandomizer.utilties

import com.thebipolaroptimist.stuffrandomizer.data.Category
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.util.UUID

class PartiesTest {

    @Test
    fun roll_lessAssigneesThanThings() {
        val assignees = listOf("A", "B")
        val category1 = Category(UUID.randomUUID(), "Category1",
            listOf("Thing1", "Thing2", "Thing3"))
        val category2 = Category(UUID.randomUUID(), "Category2",
            listOf("Cat2-1", "Cat2-2", "Cat2-3"))

        val result = Parties.roll(assignees, listOf(category1, category2))

        assertEquals(assignees.size, result.size)

        val resultAssignees = ArrayList<String>()
        for(r in result) {
            assertEquals(2, r.assignments.size)
            assertTrue(assignees.contains(r.assignee))

            resultAssignees.add(r.assignee)
        }

        assertTrue(resultAssignees.containsAll(assignees))
    }

    @Test
    fun roll_moreAssigneesThanThings() {
        val assignees = listOf("A", "B", "C", "D")
        val category1 = Category(UUID.randomUUID(), "Category1",
            listOf("Thing1", "Thing2", "Thing3"))
        val category2 = Category(UUID.randomUUID(), "Category2",
            listOf("Cat2-1", "Cat2-2", "Cat2-3"))

        val result = Parties.roll(assignees, listOf(category1, category2))

        assertEquals(assignees.size, result.size)

        val resultAssignees = ArrayList<String>()
        for(r in result) {
            assertEquals(2, r.assignments.size)
            assertTrue(assignees.contains(r.assignee))

            resultAssignees.add(r.assignee)
        }

        assertTrue(resultAssignees.containsAll(assignees))
    }

    @Test
    fun buildParty_nothingChecked_returnsNull() {
        val category1 = Category(UUID.randomUUID(), "Category1", listOf("Thing1", "Thing2"))
        val category2 = Category(UUID.randomUUID(), "Category2", listOf("Cat2-1", "Cat2-2"))

        val result = Parties.buildParty(
            "Party",
            assigneeCategoryIndex = 0,
            checkedState = listOf(false, false),
            categoryList = listOf(category1, category2)
        )

        assertEquals(null, result)
    }

    @Test
    fun buildParty_assigneeIndexOutOfRange_returnsNull() {
        val category1 = Category(UUID.randomUUID(), "Category1", listOf("Thing1", "Thing2"))

        val result = Parties.buildParty(
            "Party",
            assigneeCategoryIndex = 5,
            checkedState = listOf(true),
            categoryList = listOf(category1)
        )

        assertEquals(null, result)
    }

    @Test
    fun buildParty_checkedCategories_buildsPartyFromSelection() {
        val assigneeCategory = Category(UUID.randomUUID(), "Assignees", listOf("A", "B"))
        val category1 = Category(UUID.randomUUID(), "Category1", listOf("Thing1", "Thing2"))
        val category2 = Category(UUID.randomUUID(), "Category2", listOf("Cat2-1", "Cat2-2"))

        val result = Parties.buildParty(
            "Party",
            assigneeCategoryIndex = 0,
            checkedState = listOf(false, true, false),
            categoryList = listOf(assigneeCategory, category1, category2)
        )

        assertEquals("Party", result?.partyName)
        assertEquals("Assignees", result?.assigneeList)
        assertEquals(2, result?.members?.size)
        assertTrue(result!!.members.all { it.assignments.size == 1 && it.assignments.containsKey("Category1") })
    }
}