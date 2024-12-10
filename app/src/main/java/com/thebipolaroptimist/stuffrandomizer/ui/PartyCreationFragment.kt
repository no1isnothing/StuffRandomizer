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
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [RecyclerView.Adapter] for displaying selectable [Category]s to be used to create the [Party]
 */
class SelectableCategoryAdapter(private val categoryList: List<Category>,
                                private val checkboxStateList: ArrayList<Boolean>) :
RecyclerView.Adapter<SelectableCategoryAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.selectableCategoryName)
        val checkBox: CheckBox = view.findViewById(R.id.categoryCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_select, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = categoryList[position].name
        holder.checkBox.isChecked = checkboxStateList[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkboxStateList[position] = isChecked
        }
    }
    override fun getItemCount() = categoryList.size

}

/**
 * A [Fragment] for creating [Party]s.
 */
@AndroidEntryPoint
class PartyCreationFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentPartyCreationBinding? = null

    private val categoryList = arrayListOf<Category>()
    private val checkboxStateList = arrayListOf<Boolean>()
    private val categoryNamesList = arrayListOf<String>()

    private val categoryAdapter = SelectableCategoryAdapter(categoryList, checkboxStateList)
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
        recyclerView.adapter = categoryAdapter

        binding.assigneeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNamesList)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set values from viewmodel
        if(mainViewModel.inProgressPartyName != null) {
            binding.partyNameText.setText(mainViewModel.inProgressPartyName)
            mainViewModel.inProgressPartyName = null
        }

        mainViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryList.clear()
            categoryNamesList.clear()

            categoryList.addAll(categories)
            categoryNamesList.addAll(categoryList.map { category -> category.name })
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
            if(categoryList.size > checkboxStateList.size) {
                val diff = categories.size - checkboxStateList.size
                checkboxStateList.addAll(Array(diff) { false })
            }
            // TODO #15: Use DiffUtil to update lists.
            categoryAdapter.notifyDataSetChanged()
            arrayAdapter.notifyDataSetChanged()
        }

        binding.buttonRoll.setOnClickListener {
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

        val selectedCategoryList = ArrayList<Category>()
        var assigneeList: Category? = null
        val assigneeListName = binding.assigneeSpinner.selectedItem.toString()

        for((index, checkBoxState) in checkboxStateList.withIndex()){
            if(checkBoxState) {
                selectedCategoryList.add(categoryList[index])
            }
            if(categoryList[index].name == assigneeListName) {
                assigneeList = categoryList[index]
            }
        }
        if(selectedCategoryList.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_match_assignments_empty), Toast.LENGTH_SHORT)
                    .show()
                return false
        }
        assigneeList?.let {
            val party = Parties.create(name, it, selectedCategoryList)
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