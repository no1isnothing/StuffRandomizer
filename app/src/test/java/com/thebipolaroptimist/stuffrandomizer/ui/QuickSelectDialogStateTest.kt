package com.thebipolaroptimist.stuffrandomizer.ui

import com.thebipolaroptimist.stuffrandomizer.data.Category
import junit.framework.TestCase.assertEquals
import org.junit.Test

class QuickSelectDialogStateTest {

    @Test
    fun initialState_isDefaultWithOneBlankItem() {
        val state = QuickSelectDialogState()

        assertEquals(QuickSelectState.DEFAULT, state.selectState)
        assertEquals(listOf(""), state.newList)
        assertEquals("", state.selectedItem)
    }

    @Test
    fun startNewList_movesToListCreate() {
        val state = QuickSelectDialogState()

        state.startNewList()

        assertEquals(QuickSelectState.LIST_CREATE, state.selectState)
    }

    @Test
    fun showListSelect_movesToListSelect() {
        val state = QuickSelectDialogState()

        state.showListSelect()

        assertEquals(QuickSelectState.LIST_SELECT, state.selectState)
    }

    @Test
    fun selectExistingList_loadsCategoryThingsAndMovesToListCreate() {
        val state = QuickSelectDialogState()
        val category = Category(name = "Category", things = listOf("A", "B"))

        state.selectExistingList(category)

        assertEquals(listOf("A", "B"), state.newList)
        assertEquals(QuickSelectState.LIST_CREATE, state.selectState)
    }

    @Test
    fun addBlankItem_appendsEmptyString() {
        val state = QuickSelectDialogState()

        state.addBlankItem()

        assertEquals(listOf("", ""), state.newList)
    }

    @Test
    fun updateItem_setsValueAtIndex() {
        val state = QuickSelectDialogState()
        state.addBlankItem()

        state.updateItem(1, "Item")

        assertEquals(listOf("", "Item"), state.newList)
    }

    @Test
    fun updateItem_outOfRangeIndex_doesNothing() {
        val state = QuickSelectDialogState()

        state.updateItem(5, "Item")

        assertEquals(listOf(""), state.newList)
    }

    @Test
    fun removeItem_removesMatchingValue() {
        val state = QuickSelectDialogState()
        state.updateItem(0, "Item1")
        state.addBlankItem()
        state.updateItem(1, "Item2")

        state.removeItem("Item1")

        assertEquals(listOf("Item2"), state.newList)
    }

    @Test
    fun rollSelection_withOneItem_selectsItAndShowsSelected() {
        val state = QuickSelectDialogState()
        state.updateItem(0, "Only Item")

        state.rollSelection()

        assertEquals("Only Item", state.selectedItem)
        assertEquals(QuickSelectState.SHOW_SELECTED, state.selectState)
    }

    @Test
    fun rollSelection_withOnlyBlankItems_doesNothing() {
        val state = QuickSelectDialogState()

        state.rollSelection()

        assertEquals("", state.selectedItem)
        assertEquals(QuickSelectState.DEFAULT, state.selectState)
    }
}
