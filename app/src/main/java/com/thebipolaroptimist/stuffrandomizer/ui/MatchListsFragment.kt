package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.MatchSet
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentMatchListsBinding
import com.google.common.flogger.FluentLogger
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [RecyclerView.Adapter] for displaying [MatchSet]s.
 */
class MatchSetsAdapter(private val dataSet: ArrayList<MatchSet>) :
    RecyclerView.Adapter<MatchSetsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.matchsetTitle)
        val previewView: TextView = view.findViewById(R.id.matchAssigneesPreview)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.matchset_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        logger.atInfo().log("Binding view")
        viewHolder.titleView.text = dataSet[position].matchName
        viewHolder.previewView.text =
            dataSet[position].matches.joinToString { match -> match.assignee }
        viewHolder.titleView.setOnClickListener { v ->
            val bundle = Bundle()
            bundle.putString(v.resources.getString(R.string.key_uuid),
                dataSet[position].uid.toString())
            v.findNavController()
                .navigate(R.id.action_MatchListsFragment_to_MatchEditFragment,
                    bundle)
        }
    }

    override fun getItemCount() = dataSet.size

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}

/**
 * A [Fragment] for displaying [MatchSet]s.
 */
@AndroidEntryPoint
class MatchListsFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentMatchListsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var matchList = arrayListOf<MatchSet>()
    private val customAdapter = MatchSetsAdapter(matchList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchListsBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.matchSetList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = customAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.matches.observe(viewLifecycleOwner) { matches ->
            matchList.clear()
            logger.atInfo().log("Setting Matches size %d", matches.size)
            matchList.addAll(matches)
            customAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}