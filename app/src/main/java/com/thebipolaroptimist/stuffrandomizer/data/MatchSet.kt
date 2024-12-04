package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class MatchSet(
    @PrimaryKey val uid : UUID,
    val matchName: String,
    val matches: List<Match>,
    val assigneeList: String,
)