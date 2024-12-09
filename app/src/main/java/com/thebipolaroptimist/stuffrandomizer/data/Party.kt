package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A data class representing a group of [Member]s that make up a [Party]
 * partyName - The name of this party
 * members - The members of this party with their assignments
 * assigneeList - The name of the stuff used as assignees for creating the [Member]s of this [Party]
 */
@Entity
data class Party(
    @PrimaryKey val uid : UUID,
    val partyName: String,
    val members: List<Member>,
    val assigneeList: String,
)