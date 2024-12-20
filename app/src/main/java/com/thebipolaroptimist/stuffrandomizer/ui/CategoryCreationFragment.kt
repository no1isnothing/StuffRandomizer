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
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentCategoryCreationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

/**
 * A [RecyclerView.Adapter] for displaying things to be used in newly created [Category].
 */
class NewThingAdapter(private val thingList: List<String>) :
    RecyclerView.Adapter<NewThingAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thing: TextView = view.findViewById(R.id.newThing)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thing_new, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.thing.text = thingList[position]
    }
    override fun getItemCount() = thingList.size
}

/**
 * A [Fragment] to create [Category]s.
 */
@AndroidEntryPoint
class CategoryCreationFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentCategoryCreationBinding? = null

    private val newThingList = arrayListOf<String>()
    private val thingAdapter = NewThingAdapter(newThingList)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.atInfo().log("on create view")
        _binding = FragmentCategoryCreationBinding.inflate(inflater, container, false)

        if(mainViewModel.inProgressCategory != null) {
            newThingList.addAll(mainViewModel.inProgressCategory!!.things)
            binding.newStuffName.setText(mainViewModel.inProgressCategory!!.name)
            mainViewModel.inProgressCategory = null
        }
        val recyclerView = binding.newStuffList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = thingAdapter

        binding.addThing.setOnClickListener {
            logger.atInfo().log("Add Thing clicked")
            val newThingText = binding.thingText.text.toString()
            if (newThingText.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_empty_item), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (newThingList.contains(newThingText)) {
                Toast.makeText(context, getString(R.string.warning_duplicate_item), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            newThingList.add(binding.thingText.text.toString())
            thingAdapter.notifyDataSetChanged()
            binding.thingText.text.clear()
        }
        binding.saveStuff.setOnClickListener {
            val nameText = binding.newStuffName.text.toString()
            if (nameText.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_list_name_empty), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (newThingList.isEmpty()) {
                Toast.makeText(context, getString(R.string.warning_empty_item_list), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            mainViewModel.insertCategory(Category(UUID.randomUUID(), nameText, newThingList))
            findNavController().popBackStack()
        }

        binding.cancelStuffCreation.setOnClickListener {
            launchDiscardDialog()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        logger.atInfo().log("On stop")
        if(binding.newStuffName.text.isNotEmpty() || newThingList.isNotEmpty()) {
            val category = Category(TEMP_UUID, binding.newStuffName.text.toString(), newThingList)
            mainViewModel.inProgressCategory = category

            logger.atInfo().log(mainViewModel.inProgressCategory!!.name)
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