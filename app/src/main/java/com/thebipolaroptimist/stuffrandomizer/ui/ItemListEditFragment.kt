package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.ItemList
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentItemListEditBinding
import com.thebipolaroptimist.stuffrandomizer.ui.ItemListCreationFragment.Companion.TEMP_UUID
import java.util.UUID


/**
 * A [RecyclerView.Adapter] for displaying strings to be used for displaying and editing [ItemList].
 */
class EditItemListAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<EditItemListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_name_view)
        val editText: EditText = view.findViewById((R.id.item_name_edit))
        val editButton: ImageButton = view.findViewById(R.id.item_edit_button)
        val deleteButton: ImageButton = view.findViewById(R.id.item_delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemlist_item_edit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
        holder.editButton.setOnClickListener {
            if (holder.textView.isVisible) {
                holder.textView.visibility = GONE
                holder.editText.setText(holder.textView.text)
                holder.editText.visibility = VISIBLE
                holder.editButton.setImageResource(android.R.drawable.ic_menu_save)
            } else {
                holder.textView.text = holder.editText.text
                items[position] = holder.editText.text.toString()
                holder.textView.visibility = VISIBLE
                holder.editText.visibility = GONE
                holder.editButton.setImageResource(android.R.drawable.ic_menu_edit)
            }
        }

        holder.deleteButton.setOnClickListener {
            items.removeAt(position)
            this.notifyDataSetChanged()
        }
    }

    override fun getItemCount() = items.size
}

/**
 * A simple [Fragment] to edit [ItemList]s.
 */
class ItemListEditFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentItemListEditBinding? = null

    private val editItemList = arrayListOf<String>()
    private val adapter = EditItemListAdapter(editItemList)

    private val binding get() = _binding!!
    private var uuid = TEMP_UUID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListEditBinding.inflate(inflater, container, false)

        uuid = UUID.fromString(arguments?.getString(resources.getString(R.string.key_uuid)))

        val listWithId =
            mainViewModel.itemLists
                .value?.filter { itemList -> itemList.uid == uuid }
        listWithId?.get(0)?.let {
            editItemList.addAll(it.items)
            binding.editItemListName.setText(it.listName)
        }

        val recyclerView = binding.editItemList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        binding.editAddItem.setOnClickListener {
            editItemList.add("")
            adapter.notifyDataSetChanged()
        }

        binding.editItemDiscard.setOnClickListener {
            editItemList.clear()
            listWithId?.get(0)?.let {
                editItemList.addAll(it.items)
                adapter.notifyDataSetChanged()
                binding.editItemListName.setText(it.listName)
            }
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        logger.atInfo().log("On stop")
        if(binding.editItemListName.text.isNotEmpty() || editItemList.isNotEmpty()) {
            val itemList = ItemList(uuid, binding.editItemListName.text.toString(), editItemList)
            mainViewModel.insertItemList(itemList)
        }
    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}