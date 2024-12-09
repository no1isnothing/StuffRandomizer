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
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentPartyCreationBinding
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Stuff
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [RecyclerView.Adapter] for displaying selectable [Stuff]s to be used to create the [Party]
 */
class SelectableStuffAdapter(private val stuffs: List<Stuff>,
                             private val checkboxState: ArrayList<Boolean>) :
RecyclerView.Adapter<SelectableStuffAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listName = view.findViewById<TextView>(R.id.itemlist_checkbox_name)
        val checkBox = view.findViewById<CheckBox>(R.id.itemlist_checkbox_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stuff_item_select, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.listName.text = stuffs[position].name
        holder.checkBox.isChecked = checkboxState[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkboxState[position] = isChecked
        }
    }
    override fun getItemCount() = stuffs.size

}

/**
 * A [Fragment] for creating [Party]s.
 */
@AndroidEntryPoint
class PartyCreationFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentPartyCreationBinding? = null

    private var stuffs = arrayListOf<Stuff>()
    private var checkboxStates = arrayListOf<Boolean>()
    private var listNames = arrayListOf<String>()

    private var adapter = SelectableStuffAdapter(stuffs, checkboxStates)
    private var matchSaved = false

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPartyCreationBinding.inflate(inflater, container, false)
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
            stuffs.clear()
            listNames.clear()

            stuffs.addAll(itemsList)
            listNames.addAll(stuffs.map { itemList -> itemList.name })
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
            if(stuffs.size > checkboxStates.size) {
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

            if(createParty()) {
                findNavController().navigate(R.id.action_PartyCreationFragment_to_PartyListFragment)
            }
        }
    }

    private fun createParty(): Boolean {
        val name = binding.matchNameText.text.toString()

        if(name.isEmpty()) {
            Toast.makeText(context, getString(R.string.warning_match_name_empty), Toast.LENGTH_SHORT)
                .show()
            return false
        }

        val selectedLists = ArrayList<Stuff>()
        var assigneeList: Stuff? = null
        val assigneeListName = binding.assigneeSpinner.selectedItem.toString()

        for((index, checkBoxState) in checkboxStates.withIndex()){
            if(checkBoxState) {
                selectedLists.add(stuffs[index])
            }
            if(stuffs[index].name == assigneeListName) {
                assigneeList = stuffs[index]
            }
        }
        if(selectedLists.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_match_assignments_empty), Toast.LENGTH_SHORT)
                    .show()
                return false
        }
        assigneeList?.let {
            val matchSet = Parties.create(name, it, selectedLists)
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