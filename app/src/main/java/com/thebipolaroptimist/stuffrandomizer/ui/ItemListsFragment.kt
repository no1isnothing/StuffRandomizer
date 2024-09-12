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
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.StuffRandomizerApplication
import com.thebipolaroptimist.stuffrandomizer.data.ItemList
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentItemListsBinding
import com.google.common.flogger.FluentLogger
import dagger.hilt.android.AndroidEntryPoint


/**
 * A [RecyclerView.Adapter] for displaying [ItemList]s.
 */
class ItemListListsAdapter(private val dataSet: List<ItemList>) :
    RecyclerView.Adapter<ItemListListsAdapter.ViewHolder>() {
         class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
             val titleView: TextView = view.findViewById(R.id.itemListTitle)
             val previewView: TextView = view.findViewById(R.id.itemListPreview)
         }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context)
           .inflate(R.layout.itemlist_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleView.text = dataSet[position].listName
        holder.previewView.text = dataSet[position].items.joinToString()
    }
    override fun getItemCount() = dataSet.size
}

/**
 * A simple [Fragment] for displaying [ItemList]s.
 */
@AndroidEntryPoint
class ItemListsFragment : Fragment() {
    val logger: FluentLogger = FluentLogger.forEnclosingClass()
    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: FragmentItemListsBinding? = null

    var itemList = arrayListOf<ItemList>()
    val adapter = ItemListListsAdapter(itemList)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //TODO #6: Calls to view model for data should be more specific.
        mainViewModel.getPreviewData()
        _binding = FragmentItemListsBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.itemlist
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.itemLists.observe(requireActivity()) { itemLists ->
            logger.atInfo().log("Setting item list size %d", itemLists.size)
            itemList.addAll(itemLists)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}