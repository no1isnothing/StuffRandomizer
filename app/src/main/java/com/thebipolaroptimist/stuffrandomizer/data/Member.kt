package com.thebipolaroptimist.stuffrandomizer.data

/**
 * A data class representing a [Member].
 * @property assignee The user that is assigned things from [Category]s
 * @property assignments A map of [Category] name to things assigned to this member
 *
 * For example, for a member, Jane, that was matched with the Aedrea, Talos, and Daedra, Azura,
 * The [Member] would be ( assignee: "Jane", assignments: { "aedra" : "Talos", "daedra" : "Azura" })
 */
data class Member(val assignee: String, var assignments: MutableMap<String,String> )