package com.thebipolaroptimist.stuffrandomizer.utilties

import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import java.util.UUID

object Parties {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()

    /**
     * A function to roll assignments for [Member]s
     *
     * @param assignees Assignees to use for creating [Member]s
     * @param assignmentLists [Category]s to use for creating [Member] assignments
     *
     * @return [Member]s with provided [Category]s assigned.
     */
    fun roll(assignees: List<String>, assignmentLists: List<Category>): List<Member> {
        val members = ArrayList<Member>()
        for(assignee in assignees) {
            members.add(Member(assignee, HashMap()))
        }
        for(assignmentList in assignmentLists) {
            val shuffledAssignmentList = assignmentList.things.shuffled()

            for((index, member) in members.withIndex()) {
                val quotient = index/shuffledAssignmentList.size
                val correctedIndex = index - (quotient * shuffledAssignmentList.size)

                member.assignments.put(assignmentList.name, shuffledAssignmentList[correctedIndex])
            }
        }
        return members
    }

    /**
     * A function to create a new [Party]
     *
     * @param name Name of the new [Party]
     * @param assigneeList Assignees to use for creating [Member]s for this [Party]
     * @param assignmentLists [Category]s to use for assignments for creating [Member] assignments
     */
    fun create(name : String, assigneeList: Category, assignmentLists: List<Category>) : Party {
        val shuffledAssignees = assigneeList.things.shuffled()

        val members = roll(shuffledAssignees, assignmentLists)
        logger.atInfo().log(members.toString())

        return Party(UUID.randomUUID(), name, members, assigneeList.name)
    }
}