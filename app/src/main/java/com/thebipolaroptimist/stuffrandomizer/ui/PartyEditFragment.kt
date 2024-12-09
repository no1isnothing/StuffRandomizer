package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentPartyEditBinding
import com.thebipolaroptimist.stuffrandomizer.ui.StuffCreationFragment.Companion.TEMP_UUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


/**
 * A [RecyclerView.Adapter] for displaying [Member]s that can be edited.
 */
class EditableMemberAdapter(private val members: List<Member>) :
    RecyclerView.Adapter<EditableMemberAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val assigneeView: TextView = view.findViewById(R.id.matchAssignee)
        val assignmentView: TextView = view.findViewById(R.id.matchAssignments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.member_item_edit, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.assigneeView.text = members[position].assignee
        holder.assignmentView.text = members[position].assignments
            .map { assignment -> assignment.key + ": " + assignment.value }.joinToString()
    }

    override fun getItemCount() = members.size

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}

/**
 * A [Fragment] for editing a [Party].
 */
@AndroidEntryPoint
class PartyEditFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentPartyEditBinding? = null

    private val binding get() = _binding!!
    private var memberList = arrayListOf<Member>()
    private val matchesAdapter = EditableMemberAdapter(memberList)

    private var matchSetUuid = TEMP_UUID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPartyEditBinding.inflate(inflater, container, false)

        matchSetUuid = UUID.fromString(arguments?.getString(resources.getString(R.string.key_uuid)))
        mainViewModel.matches
            .value?.filter { matchSet -> matchSet.uid == matchSetUuid }?.get(0)?.let {
                logger.atInfo().log("matches size " + it.members.size)
                memberList.addAll(it.members)
                binding.matchSetNameText.setText(it.partyName)
            }
        binding.matchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = matchesAdapter
        }

        return binding.root
    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}