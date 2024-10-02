package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.ItemList
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentItemListCreationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

/**
 * A [RecyclerView.Adapter] for displaying strings to be used in newly created [ItemList].
 */
class NewItemListAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<NewItemListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.newItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.new_itemlist_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position]
    }
    override fun getItemCount() = dataSet.size
}

/**
 * A [Fragment] to create [ItemList]s.
 */
@AndroidEntryPoint
class ItemListCreationFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentItemListCreationBinding? = null

    private var newItemList = arrayListOf<String>()
    private val adapter = NewItemListAdapter(newItemList)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.atInfo().log("on create view")
        _binding = FragmentItemListCreationBinding.inflate(inflater, container, false)

        if(mainViewModel.inProgressItemList != null) {
            newItemList.addAll(mainViewModel.inProgressItemList!!.items)
            binding.nameText.setText(mainViewModel.inProgressItemList!!.listName)
            mainViewModel.inProgressItemList = null
        }
        val recyclerView = binding.newItemList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        binding.addItem.setOnClickListener {
            logger.atInfo().log("Add item clicked")
            val newItemText = binding.itemText.text.toString()
            if (newItemText.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_empty_item), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (newItemList.contains(newItemText)) {
                Toast.makeText(context, getString(R.string.warning_duplicate_item), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            newItemList.add(binding.itemText.text.toString())
            adapter.notifyDataSetChanged()
            binding.itemText.text.clear()
        }
        binding.saveItemList.setOnClickListener {
            val nameText = binding.nameText.text.toString()
            if (nameText.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_list_name_empty), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (newItemList.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_empty_item_list), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            mainViewModel.insertItemList(ItemList(UUID.randomUUID(), nameText, newItemList))
            findNavController().popBackStack()
        }

        binding.cancelItemListCreation.setOnClickListener {
            launchDiscardDialog()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        logger.atInfo().log("On stop")
        if(binding.nameText.text.isNotEmpty() || newItemList.isNotEmpty()) {
            val itemList = ItemList(TEMP_UUID, binding.nameText.text.toString(), newItemList)
            mainViewModel.inProgressItemList = itemList

            logger.atInfo().log(mainViewModel.inProgressItemList!!.listName)
        }
    }

    private fun launchDiscardDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.discard_draft))
            .setNegativeButton(resources.getString(R.string.cancel)) {_,_ ->}
            .setPositiveButton(resources.getString(R.string.discard)) { _,_ ->
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TEMP_UUID = UUID(0,0)
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}