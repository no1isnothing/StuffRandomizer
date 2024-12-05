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
import com.thebipolaroptimist.stuffrandomizer.data.Match
import com.thebipolaroptimist.stuffrandomizer.data.MatchSet
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentMatchEditBinding
import com.thebipolaroptimist.stuffrandomizer.ui.ItemListCreationFragment.Companion.TEMP_UUID
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID


/**
 * A [RecyclerView.Adapter] for displaying [Match]s that can be edited.
 */
class MatchListAdapter(private val matches: List<Match>) :
    RecyclerView.Adapter<MatchListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val assigneeView: TextView = view.findViewById(R.id.matchAssignee)
        val assignmentView: TextView = view.findViewById(R.id.matchAssignments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.match_item_edit, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.assigneeView.text = matches[position].assignee
        holder.assignmentView.text = matches[position].assignments
            .map { assignment -> assignment.key + ": " + assignment.value }.joinToString()
    }

    override fun getItemCount() = matches.size

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}

/**
 * A [Fragment] for editing a [MatchSet].
 */
@AndroidEntryPoint
class MatchEditFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentMatchEditBinding? = null

    private val binding get() = _binding!!
    private var matchList = arrayListOf<Match>()
    private val matchesAdapter = MatchListAdapter(matchList)

    private var matchSetUuid = TEMP_UUID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchEditBinding.inflate(inflater, container, false)

        matchSetUuid = UUID.fromString(arguments?.getString(resources.getString(R.string.key_uuid)))
        mainViewModel.matches
            .value?.filter { matchSet -> matchSet.uid == matchSetUuid }?.get(0)?.let {
                logger.atInfo().log("matches size " + it.matches.size)
                matchList.addAll(it.matches)
                binding.matchSetNameText.setText(it.matchName)
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