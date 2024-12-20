package com.thebipolaroptimist.stuffrandomizer.utilties

import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import java.util.UUID

object Parties {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()

    fun create(name : String, assigneeList: Category, assignmentLists: List<Category>) : Party {
        val assignmentListNames = assignmentLists.joinToString{ it -> it.name }
        logger.atInfo().log("Assignee List %s Assignment Lists %s", assigneeList.name, assignmentListNames )
        val shuffledAssignees = assigneeList.things.shuffled()
        assignmentLists.size

        val members = ArrayList<Member>()
        for(assignee in shuffledAssignees) {
            members.add(Member(assignee, HashMap()))
        }
        for(assignmentList in assignmentLists) {
            val shuffledAssignmentList = assignmentList.things

            for((index, member) in members.withIndex()) {
                val quotient = index/shuffledAssignmentList.size
                val correctedIndex = index - (quotient * shuffledAssignmentList.size)

                member.assignments.put(assignmentList.name, shuffledAssignmentList[correctedIndex])
            }
        }
        logger.atInfo().log(members.toString())

        return Party(UUID.randomUUID(), name, members,assigneeList.name)
    }
}