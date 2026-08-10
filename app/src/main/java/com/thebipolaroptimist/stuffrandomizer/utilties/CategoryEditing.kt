package com.thebipolaroptimist.stuffrandomizer.utilties

object CategoryEditing {

    enum class SaveOutcome {
        DELETE_EMPTY,
        WARN_NO_ITEMS,
        WARN_NO_NAME,
        SAVE,
    }

    /**
     * Decide what should happen when a category details screen is backed out of.
     *
     * @param name The category name as currently edited.
     * @param things The category's items as currently edited.
     */
    fun resolveBackAction(name: String, things: List<String>): SaveOutcome {
        return when {
            name.isEmpty() && things.isEmpty() -> SaveOutcome.DELETE_EMPTY
            things.isEmpty() -> SaveOutcome.WARN_NO_ITEMS
            name.isEmpty() -> SaveOutcome.WARN_NO_NAME
            else -> SaveOutcome.SAVE
        }
    }

    /**
     * Remove blank entries before persisting a category's items.
     */
    fun sanitizeThings(things: List<String>): List<String> = things.filter { it.isNotEmpty() }
}
