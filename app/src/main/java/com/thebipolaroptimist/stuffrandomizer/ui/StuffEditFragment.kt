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
import com.thebipolaroptimist.stuffrandomizer.data.Stuff
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentStuffEditBinding
import com.thebipolaroptimist.stuffrandomizer.ui.StuffCreationFragment.Companion.TEMP_UUID
import java.util.UUID


/**
 * A [RecyclerView.Adapter] for displaying strings to be used for displaying and editing [Stuff].
 */
class EditableThingAdapter(private val thingList: ArrayList<String>) :
    RecyclerView.Adapter<EditableThingAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.thingNameView)
        val nameEdit: EditText = view.findViewById((R.id.thingNameEdit))
        val editButton: ImageButton = view.findViewById(R.id.thingEditButton)
        val deleteButton: ImageButton = view.findViewById(R.id.thingDeleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.thing_item_edit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = thingList[position]
        holder.editButton.setOnClickListener {
            if (holder.name.isVisible) {
                holder.name.visibility = GONE
                holder.nameEdit.setText(holder.name.text)
                holder.nameEdit.visibility = VISIBLE
                holder.editButton.setImageResource(android.R.drawable.ic_menu_save)
            } else {
                holder.name.text = holder.nameEdit.text
                thingList[position] = holder.nameEdit.text.toString()
                holder.name.visibility = VISIBLE
                holder.nameEdit.visibility = GONE
                holder.editButton.setImageResource(android.R.drawable.ic_menu_edit)
            }
        }

        holder.deleteButton.setOnClickListener {
            thingList.removeAt(position)
            this.notifyDataSetChanged()
        }
    }

    override fun getItemCount() = thingList.size
}

/**
 * A simple [Fragment] to edit [Stuff]s.
 */
class StuffEditFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentStuffEditBinding? = null

    private val editThingList = arrayListOf<String>()
    private val thingAdapter = EditableThingAdapter(editThingList)

    private val binding get() = _binding!!
    private var uuid = TEMP_UUID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStuffEditBinding.inflate(inflater, container, false)

        uuid = UUID.fromString(arguments?.getString(resources.getString(R.string.key_uuid)))

        val listWithId =
            mainViewModel.stuffs
                .value?.filter { stuff -> stuff.uid == uuid }
        listWithId?.get(0)?.let {
            editThingList.addAll(it.things)
            binding.editItemListName.setText(it.name)
        }

        val recyclerView = binding.editItemList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = thingAdapter

        binding.editAddItem.setOnClickListener {
            editThingList.add("")
            thingAdapter.notifyDataSetChanged()
        }

        binding.editItemDiscard.setOnClickListener {
            editThingList.clear()
            listWithId?.get(0)?.let {
                editThingList.addAll(it.things)
                thingAdapter.notifyDataSetChanged()
                binding.editItemListName.setText(it.name)
            }
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        logger.atInfo().log("On stop")
        if(binding.editItemListName.text.isNotEmpty() || editThingList.isNotEmpty()) {
            val stuff = Stuff(uuid, binding.editItemListName.text.toString(), editThingList)
            mainViewModel.insertStuff(stuff)
        }
    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}