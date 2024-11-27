package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentMatchCreationBinding
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.ItemList
import com.thebipolaroptimist.stuffrandomizer.data.MatchSet
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [RecyclerView.Adapter] for displaying selectable [ItemList]s to be used to create the [MatchSet]
 */
class SelectableItemListsAdapter(private val itemLists: List<ItemList>,
                                 private val checkboxState: ArrayList<Boolean>) :
RecyclerView.Adapter<SelectableItemListsAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listName = view.findViewById<TextView>(R.id.itemlist_checkbox_name)
        val checkBox = view.findViewById<CheckBox>(R.id.itemlist_checkbox_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemlist_checkbox_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.listName.text = itemLists[position].listName
        holder.checkBox.isChecked = checkboxState[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkboxState[position] = isChecked
        }
    }
    override fun getItemCount() = itemLists.size

}

/**
 * A [Fragment] for creating [MatchSet]s.
 */
@AndroidEntryPoint
class MatchCreationFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentMatchCreationBinding? = null

    private var itemLists = arrayListOf<ItemList>()
    private var checkboxState = arrayListOf<Boolean>()
    private var listNames = arrayListOf<String>()

    private var adapter = SelectableItemListsAdapter(itemLists, checkboxState)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMatchCreationBinding.inflate(inflater, container, false)
        val recyclerView : RecyclerView = binding.assignmentsList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        binding.assigneeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listNames)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.itemLists.observe(requireActivity()) { itemsList ->
            itemLists.clear()
            checkboxState.clear()
            listNames.clear()

            itemLists.addAll(itemsList)
            checkboxState.addAll(Array(itemLists.size) { false } )
            listNames.addAll(itemLists.map { itemList -> itemList.listName })
            // TODO #15: Use DiffUtil to update lists.
            adapter.notifyDataSetChanged()
            (binding.assigneeSpinner.adapter as ArrayAdapter<String>).notifyDataSetChanged()
        }

        binding.buttonRoll.setOnClickListener {
            logger.atInfo().log("Check box state %s", checkboxState.joinToString())
            logger.atInfo().log("Spinner selection %s", binding.assigneeSpinner.selectedItem.toString())
            // TODO #9: Use this information to create a MatchSet and navigate to edit page for it.
            findNavController().navigate(R.id.action_MatchCeationFragment_to_MatchEditFragment)
        }
    }

    // TODO #9: Add onStop to save in progress data to ViewModel and update init code to use it.

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}