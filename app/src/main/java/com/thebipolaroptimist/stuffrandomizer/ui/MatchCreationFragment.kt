package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
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
import com.thebipolaroptimist.stuffrandomizer.utilties.MatchSets
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
    private var checkboxStates = arrayListOf<Boolean>()
    private var listNames = arrayListOf<String>()

    private var adapter = SelectableItemListsAdapter(itemLists, checkboxStates)
    private var matchSaved = false

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
        // set values from viewmodel
        if(mainViewModel.inProgressMatchName != null) {
            binding.matchNameText.setText(mainViewModel.inProgressMatchName)
            mainViewModel.inProgressMatchName = null
        }

        mainViewModel.itemLists.observe(viewLifecycleOwner) { itemsList ->
            itemLists.clear()
            listNames.clear()

            itemLists.addAll(itemsList)
            listNames.addAll(itemLists.map { itemList -> itemList.listName })
            val arrayAdapter = (binding.assigneeSpinner.adapter as ArrayAdapter<String>)
            if(mainViewModel.inProgressCheckBoxState != null) {
                checkboxStates.addAll(mainViewModel.inProgressCheckBoxState!!)
                mainViewModel.inProgressCheckBoxState = null
            }
            if(mainViewModel.inProgressAssigneeSelection != null) {
                binding.assigneeSpinner
                    .setSelection(arrayAdapter.getPosition(mainViewModel.inProgressAssigneeSelection))
                mainViewModel.inProgressAssigneeSelection = null
            }
            if(itemLists.size > checkboxStates.size) {
                val diff = itemsList.size - checkboxStates.size
                checkboxStates.addAll(Array(diff) { false })
            }
            // TODO #15: Use DiffUtil to update lists.
            adapter.notifyDataSetChanged()
            arrayAdapter.notifyDataSetChanged()
        }

        binding.buttonRoll.setOnClickListener {
            logger.atInfo().log("Check box state %s", checkboxStates.joinToString())
            logger.atInfo().log("Spinner selection %s", binding.assigneeSpinner.selectedItem.toString())

            if(createMatchSets()) {
                findNavController().navigate(R.id.action_MatchCreationFragment_to_MatchListsFragment)
            }
        }
    }

    private fun createMatchSets(): Boolean {
        val name = binding.matchNameText.text.toString()

        if(name.isEmpty()) {
            Toast.makeText(context, getString(R.string.warning_match_name_empty), Toast.LENGTH_SHORT)
                .show()
            return false
        }

        val selectedLists = ArrayList<ItemList>()
        var assigneeList: ItemList? = null
        val assigneeListName = binding.assigneeSpinner.selectedItem.toString()

        for((index, checkBoxState) in checkboxStates.withIndex()){
            if(checkBoxState) {
                selectedLists.add(itemLists[index])
            }
            if(itemLists[index].listName == assigneeListName) {
                assigneeList = itemLists[index]
            }
        }
        if(selectedLists.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_match_assignments_empty), Toast.LENGTH_SHORT)
                    .show()
                return false
        }
        assigneeList?.let {
            val matchSet = MatchSets.create(name, it, selectedLists)
            mainViewModel.insertMatchSet(matchSet)
            matchSaved = true
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        if(matchSaved) {
            return
        }

        if(binding.matchNameText.text.isNotEmpty()) {
            mainViewModel.inProgressMatchName = binding.matchNameText.text.toString()
        }
        mainViewModel.inProgressCheckBoxState = checkboxStates
        mainViewModel.inProgressAssigneeSelection = binding.assigneeSpinner.selectedItem.toString()
    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}