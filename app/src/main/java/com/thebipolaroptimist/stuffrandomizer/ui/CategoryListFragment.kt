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
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentCategoryListBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [RecyclerView.Adapter] for displaying [Category]s.
 */
class CategoryListAdapter(private val categoryList: List<Category>) :
    RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.categoryName)
        val preview: TextView = view.findViewById(R.id.thingListPreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thing, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = categoryList[position].name
        holder.preview.text = categoryList[position].things.joinToString()

        holder.name.setOnClickListener { v ->
            val bundle = Bundle()
            bundle.putString(
                v.resources.getString(R.string.key_uuid),
                categoryList[position].uid.toString()
            )
            v.findNavController()
                .navigate(
                    R.id.action_CategoryListFragment_to_CategoryEditFragment,
                    bundle
                )
        }
    }

    override fun getItemCount() = categoryList.size

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}

/**
 * A simple [Fragment] for displaying [Category]s.
 */
@AndroidEntryPoint
class CategoryListFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentCategoryListBinding? = null

    private val categoryList = arrayListOf<Category>()
    private val categoryAdapter = CategoryListAdapter(categoryList)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.itemlist
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = categoryAdapter

        binding.categoryFab.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_CategoryListFragment_to_CategoryCreationFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.categories.observe(viewLifecycleOwner) { itemLists ->
            categoryList.clear()
            logger.atInfo().log("Setting item list size %d", itemLists.size)
            categoryList.addAll(itemLists)
            categoryAdapter.notifyDataSetChanged()
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