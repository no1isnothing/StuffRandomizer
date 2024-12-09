package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Stuff
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentStuffListBinding
import com.google.common.flogger.FluentLogger
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [RecyclerView.Adapter] for displaying [Stuff]s.
 */
class StuffListAdapter(private val stuffList: List<Stuff>) :
    RecyclerView.Adapter<StuffListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.stuffName)
        val preview: TextView = view.findViewById(R.id.thingListPreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.thing_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = stuffList[position].name
        holder.preview.text = stuffList[position].things.joinToString()

        holder.name.setOnClickListener { v ->
            val bundle = Bundle()
            bundle.putString(
                v.resources.getString(R.string.key_uuid),
                stuffList[position].uid.toString()
            )
            v.findNavController()
                .navigate(
                    R.id.action_StuffListFragment_to_StuffEditFragment,
                    bundle
                )
        }
    }

    override fun getItemCount() = stuffList.size

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}

/**
 * A simple [Fragment] for displaying [Stuff]s.
 */
@AndroidEntryPoint
class StuffListFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentStuffListBinding? = null

    private var stuffList = arrayListOf<Stuff>()
    private val stuffAdapter = StuffListAdapter(stuffList)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStuffListBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.itemlist
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = stuffAdapter

        binding.stuffFab.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_StuffListFragment_to_StuffCreationFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.stuffs.observe(viewLifecycleOwner) { itemLists ->
            stuffList.clear()
            logger.atInfo().log("Setting item list size %d", itemLists.size)
            stuffList.addAll(itemLists)
            stuffAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}