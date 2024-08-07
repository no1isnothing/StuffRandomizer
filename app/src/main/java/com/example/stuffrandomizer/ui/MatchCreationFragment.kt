package com.example.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stuffrandomizer.databinding.FragmentMatchCreationBinding
import com.example.stuffrandomizer.R

/**
 * A simple [Fragment] subclass.
 * Use the [MatchCreationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MatchCreationFragment : Fragment() {
    private var _binding: FragmentMatchCreationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMatchCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonToHome.setOnClickListener {
            findNavController().navigate(R.id.action_MatchCeationFragment_to_HomeFragment)
        }
        binding.buttonToEdit.setOnClickListener {
            findNavController().navigate(R.id.action_MatchCeationFragment_to_MatchEditFragment)
        }
    }

}