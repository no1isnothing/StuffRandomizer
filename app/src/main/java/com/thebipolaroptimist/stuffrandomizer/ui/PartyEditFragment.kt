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
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryCreationFragment.Companion.TEMP_UUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


/**
 * A [RecyclerView.Adapter] for displaying [Member]s that can be edited.
 */
class EditableMemberAdapter(private val memberList: List<Member>) :
    RecyclerView.Adapter<EditableMemberAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val assignee: TextView = view.findViewById(R.id.memberAssignee)
        val assignment: TextView = view.findViewById(R.id.memberAssignments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_edit, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.assignee.text = memberList[position].assignee
        holder.assignment.text = memberList[position].assignments
            .map { assignment -> assignment.key + ": " + assignment.value }.joinToString()
    }

    override fun getItemCount() = memberList.size

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
    private val memberList = arrayListOf<Member>()
    private val memberAdapter = EditableMemberAdapter(memberList)

    private var partyUuid = TEMP_UUID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPartyEditBinding.inflate(inflater, container, false)

        partyUuid = UUID.fromString(arguments?.getString(resources.getString(R.string.key_uuid)))
        mainViewModel.parties
            .value?.filter { parties -> parties.uid == partyUuid }?.get(0)?.let {
                logger.atInfo().log("Members size " + it.members.size)
                memberList.addAll(it.members)
                binding.partyNameText.setText(it.partyName)
            }
        binding.memberList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = memberAdapter
        }

        return binding.root
    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}