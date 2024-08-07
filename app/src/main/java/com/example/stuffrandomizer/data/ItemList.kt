package com.example.stuffrandomizer.data

/**
 * A data class representing a list that can be used to create a match.
 *
 * For example, a list of Aedra would be:
 * (listName: "Aedra", items: ["Talos", "Julianos", ..., "Arkay", "Akatosh"])
 */
data class ItemList(val listName: String, var items: List<String>)