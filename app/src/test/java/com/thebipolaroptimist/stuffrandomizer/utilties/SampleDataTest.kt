package com.thebipolaroptimist.stuffrandomizer.utilties

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class SampleDataTest {

    @Test
    fun createDefaultCategories_returnsRacesBackgroundsAndClasses() {
        val races = listOf("Human", "Elf")
        val backgrounds = listOf("Noble", "Orphan")
        val classes = listOf("Warrior", "Mage")

        val result = SampleData.createDefaultCategories(races, backgrounds, classes)

        assertEquals(3, result.size)
        assertEquals(races, result.first { it.name == "BG Races" }.things)
        assertEquals(backgrounds, result.first { it.name == "BG Backgrounds" }.things)
        assertEquals(classes, result.first { it.name == "BG Classes" }.things)
    }

    @Test
    fun createSamplePartyData_returnsNonEmptyParty() {
        val party = SampleData.createSamplePartyData()

        assertEquals("Skyrim 2024", party.partyName)
        assertEquals("Friends", party.assigneeList)
        assertTrue(party.members.isNotEmpty())
    }

    @Test
    fun createSampleCategoryData_returnsThreeCategories() {
        val categories = SampleData.createSampleCategoryData()

        assertEquals(3, categories.size)
        assertTrue(categories.all { it.things.isNotEmpty() })
    }
}
