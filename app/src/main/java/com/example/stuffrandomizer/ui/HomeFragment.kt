package com.example.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stuffrandomizer.databinding.FragmentHomeBinding
import com.example.stuffrandomizer.R
import com.example.stuffrandomizer.StuffRandomizerApplication

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.Companion.MainViewModelFactory((requireActivity().application as StuffRandomizerApplication).repository)
    }
    private var _binding: FragmentHomeBinding? = null

    // TODO #5: keep an eye out for null pointer exception here on back.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //TODO #6: Calls to view model for data should be more specific.
        mainViewModel.getPreviewData()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonItems.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_ItemListsFragment)
        }
        binding.buttonMatches.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_MatchListsFragment)
        }


        mainViewModel.previewString.observe(requireActivity()) { value ->
            binding.matchesTextPreview.text = value
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}