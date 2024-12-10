package com.thebipolaroptimist.stuffrandomizer.data

/**
 * A data class representing a [Member].
 * assignee - The user that is assigned this stuff
 * assignments - A map of stuff name to stuff assigned to this member
 *
 * For example, for a member, Jane, that was matched with the Aedrea, Talos, and Daedra, Azura,
 * The [Member] would be ( assignee: "Jane", assignments: { "aedra" : "Talos", "daedra" : "Azura" })
 */
data class Member(val assignee: String, var assignments: MutableMap<String,String> )