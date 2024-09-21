package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: FragmentItemListCreationBinding? = null

    private var newItemList = arrayListOf<String>()
    private val adapter = NewItemListAdapter(newItemList)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentItemListCreationBinding.inflate(inflater, container, false)

        val recyclerView = binding.newItemList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        binding.addItem.setOnClickListener {
            logger.atInfo().log("Add item clicked")
            // TODO: check text, can't be empty
            val newItemText = binding.itemText.text.toString()
            if(newItemText.isEmpty()) {
                return@setOnClickListener
            }
            if(newItemList.contains(newItemText)) {
                // pop up duplicate toast
                return@setOnClickListener
            }
            newItemList.add(binding.itemText.text.toString())
            adapter.notifyDataSetChanged()
            binding.itemText.text.clear()
        }
        binding.saveItemList.setOnClickListener {
            val nameText = binding.nameText.text.toString()
            if(nameText.isEmpty() || newItemList.isEmpty()) {
                return@setOnClickListener
            }
            mainViewModel.insertItemList(ItemList(UUID.randomUUID(), nameText, newItemList))

            // navigate back or clear?
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}