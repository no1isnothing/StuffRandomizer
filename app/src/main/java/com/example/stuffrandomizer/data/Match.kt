package com.example.stuffrandomizer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// blue lagoon

/**
 * A data class representing a match
 * assignee - The user that is assigned these things
 * matches - A map of list name to list item assigned to the assignee
 *
 * For example, for a player that was matched with the Aedrea Talos and Daedra Azura
 * A match would be ( assignee: "Jane", matches: { "aedra" : "Talos", "daedra" : "Azura" })
 */
data class Match(val assignee: String, var matches: Map<String,String> )