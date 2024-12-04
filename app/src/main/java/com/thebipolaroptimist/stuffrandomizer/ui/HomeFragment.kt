package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentHomeBinding
import com.thebipolaroptimist.stuffrandomizer.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null

    // TODO #5: keep an eye out for null pointer exception here on back.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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


        mainViewModel.matches.observe(viewLifecycleOwner) { matches ->
            if(matches.isNotEmpty()) {
                binding.matchesTextPreview.text = matches[0].matchName
            }
        }

        binding.homeFab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_HomeFragment_to_MatchCreationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}