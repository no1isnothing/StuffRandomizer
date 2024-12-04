package com.thebipolaroptimist.stuffrandomizer.utilties

import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.data.ItemList
import com.thebipolaroptimist.stuffrandomizer.data.Match
import com.thebipolaroptimist.stuffrandomizer.data.MatchSet
import java.util.UUID

object MatchSets {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()

    fun create(name : String, assigneeList: ItemList, assignmentLists: List<ItemList>) : MatchSet {
        val assignmentListNames = assignmentLists.joinToString{ it -> it.listName }
        logger.atInfo().log("Assignee List %s Assignment Lists %s", assigneeList.listName, assignmentListNames )
        val shuffledAssignees = assigneeList.items.shuffled()
        assignmentLists.size

        val matches = ArrayList<Match>()
        for(assignee in shuffledAssignees) {
            matches.add(Match(assignee, HashMap()))
        }
        for(assignmentList in assignmentLists) {
            val shuffledAssignmentList = assignmentList.items

            for((index, match) in matches.withIndex()) {
                val quotient = index/shuffledAssignmentList.size
                var correctedIndex = index - (quotient * shuffledAssignmentList.size)

                match.assignments.put(assignmentList.listName, shuffledAssignmentList[correctedIndex])
            }
        }
        logger.atInfo().log(matches.toString())

        return MatchSet(UUID.randomUUID(), name, matches,assigneeList.listName)
    }
}