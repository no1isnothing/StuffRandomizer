package com.example.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stuffrandomizer.R
import com.example.stuffrandomizer.StuffRandomizerApplication
import com.example.stuffrandomizer.data.MatchSet
import com.example.stuffrandomizer.databinding.FragmentMatchListsBinding
import com.google.common.flogger.FluentLogger


class MatchListsAdapter(private val dataSet: ArrayList<MatchSet>) :
    RecyclerView.Adapter<MatchListsAdapter.ViewHolder>() {
    val logger: FluentLogger = FluentLogger.forEnclosingClass()

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val previewView: TextView

        init {
            // Define click listener for the ViewHolder's View
            titleView = view.findViewById(R.id.matchsetTitle)
            previewView = view.findViewById(R.id.matchAssigneesPreview)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.matchset_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        logger.atWarning().log("Binding view")
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.titleView.text = dataSet[position].matchName
        //val list = dataSet[position].matches.stream().map { match -> match.assignee }.toList()
        viewHolder.previewView.text = dataSet[position].matches.stream().map { match -> match.assignee }.toList().joinToString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}


/**
 * A simple [Fragment] subclass.
 * Use the [MatchListsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MatchListsFragment : Fragment() {
    val logger: FluentLogger = FluentLogger.forEnclosingClass()
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.Companion.MainViewModelFactory((requireActivity().application as StuffRandomizerApplication).repository)
    }

    private var _binding: FragmentMatchListsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var matchList = arrayListOf<MatchSet>()
    val customAdapter = MatchListsAdapter(matchList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.getPreviewData()
        _binding = FragmentMatchListsBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.matchlist
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = customAdapter

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.matches.observe(requireActivity()) { matches ->
            logger.atWarning().log("Setting Matches size %d", matches.size)
            matchList.addAll(matches)
            customAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}