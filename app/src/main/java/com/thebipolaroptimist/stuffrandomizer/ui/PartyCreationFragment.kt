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
class SelectableStuffAdapter(private val stuffList: List<Stuff>,
                             private val checkboxStateList: ArrayList<Boolean>) :
RecyclerView.Adapter<SelectableStuffAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.selectableStuffName)
        val checkBox: CheckBox = view.findViewById(R.id.stuffCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stuff_item_select, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = stuffList[position].name
        holder.checkBox.isChecked = checkboxStateList[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkboxStateList[position] = isChecked
        }
    }
    override fun getItemCount() = stuffList.size

}

/**
 * A [Fragment] for creating [Party]s.
 */
@AndroidEntryPoint
class PartyCreationFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentPartyCreationBinding? = null

    private var stuffList = arrayListOf<Stuff>()
    private var checkboxStateList = arrayListOf<Boolean>()
    private var stuffNamesList = arrayListOf<String>()

    private var stuffAdapter = SelectableStuffAdapter(stuffList, checkboxStateList)
    private var partySaved = false

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPartyCreationBinding.inflate(inflater, container, false)
        val recyclerView : RecyclerView = binding.assignmentsList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = stuffAdapter

        binding.assigneeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stuffNamesList)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set values from viewmodel
        if(mainViewModel.inProgressPartyName != null) {
            binding.partyNameText.setText(mainViewModel.inProgressPartyName)
            mainViewModel.inProgressPartyName = null
        }

        mainViewModel.stuffs.observe(viewLifecycleOwner) { stuffs ->
            stuffList.clear()
            stuffNamesList.clear()

            stuffList.addAll(stuffs)
            stuffNamesList.addAll(stuffList.map { stuffs -> stuffs.name })
            val arrayAdapter = (binding.assigneeSpinner.adapter as ArrayAdapter<String>)
            if(mainViewModel.inProgressCheckBoxState != null) {
                checkboxStateList.addAll(mainViewModel.inProgressCheckBoxState!!)
                mainViewModel.inProgressCheckBoxState = null
            }
            if(mainViewModel.inProgressAssigneeSelection != null) {
                binding.assigneeSpinner
                    .setSelection(arrayAdapter.getPosition(mainViewModel.inProgressAssigneeSelection))
                mainViewModel.inProgressAssigneeSelection = null
            }
            if(stuffList.size > checkboxStateList.size) {
                val diff = stuffs.size - checkboxStateList.size
                checkboxStateList.addAll(Array(diff) { false })
            }
            // TODO #15: Use DiffUtil to update lists.
            stuffAdapter.notifyDataSetChanged()
            arrayAdapter.notifyDataSetChanged()
        }

        binding.buttonRoll.setOnClickListener {
            logger.atInfo().log("Check box state %s", checkboxStateList.joinToString())
            logger.atInfo().log("Spinner selection %s", binding.assigneeSpinner.selectedItem.toString())

            if(createParty()) {
                findNavController().navigate(R.id.action_PartyCreationFragment_to_PartyListFragment)
            }
        }
    }

    private fun createParty(): Boolean {
        val name = binding.partyNameText.text.toString()

        if(name.isEmpty()) {
            Toast.makeText(context, getString(R.string.warning_match_name_empty), Toast.LENGTH_SHORT)
                .show()
            return false
        }

        val selectedStuffList = ArrayList<Stuff>()
        var assigneeList: Stuff? = null
        val assigneeListName = binding.assigneeSpinner.selectedItem.toString()

        for((index, checkBoxState) in checkboxStateList.withIndex()){
            if(checkBoxState) {
                selectedStuffList.add(stuffList[index])
            }
            if(stuffList[index].name == assigneeListName) {
                assigneeList = stuffList[index]
            }
        }
        if(selectedStuffList.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_match_assignments_empty), Toast.LENGTH_SHORT)
                    .show()
                return false
        }
        assigneeList?.let {
            val party = Parties.create(name, it, selectedStuffList)
            mainViewModel.insertParty(party)
            partySaved = true
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        if(partySaved) {
            return
        }

        if(binding.partyNameText.text.isNotEmpty()) {
            mainViewModel.inProgressPartyName = binding.partyNameText.text.toString()
        }
        mainViewModel.inProgressCheckBoxState = checkboxStateList
        mainViewModel.inProgressAssigneeSelection = binding.assigneeSpinner.selectedItem.toString()
    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}