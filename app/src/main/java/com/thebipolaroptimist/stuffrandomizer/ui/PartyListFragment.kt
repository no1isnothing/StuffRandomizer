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
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentPartyListBinding
import com.google.common.flogger.FluentLogger
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [RecyclerView.Adapter] for displaying [Party]s.
 */
class PartyListAdapter(private val partyList: ArrayList<Party>) :
    RecyclerView.Adapter<PartyListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partyName: TextView = view.findViewById(R.id.partyName)
        val assigneePreview: TextView = view.findViewById(R.id.partyAssigneesPreview)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.party_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        logger.atInfo().log("Binding view")
        viewHolder.partyName.text = partyList[position].partyName
        viewHolder.assigneePreview.text =
            partyList[position].members.joinToString { member -> member.assignee }
        viewHolder.partyName.setOnClickListener { v ->
            val bundle = Bundle()
            bundle.putString(v.resources.getString(R.string.key_uuid),
                partyList[position].uid.toString())
            v.findNavController()
                .navigate(R.id.action_PartyListFragment_to_PartyEditFragment,
                    bundle)
        }
    }

    override fun getItemCount() = partyList.size

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}

/**
 * A [Fragment] for displaying [Party]s.
 */
@AndroidEntryPoint
class PartyListFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentPartyListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var partyList = arrayListOf<Party>()
    private val partyAdapter = PartyListAdapter(partyList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPartyListBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.partyList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = partyAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.parties.observe(viewLifecycleOwner) { parties ->
            partyList.clear()
            logger.atInfo().log("Setting Parties size %d", parties.size)
            partyList.addAll(parties)
            partyAdapter.notifyDataSetChanged()
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