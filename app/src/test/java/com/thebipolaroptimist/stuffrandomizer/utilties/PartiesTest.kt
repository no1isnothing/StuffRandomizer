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
}