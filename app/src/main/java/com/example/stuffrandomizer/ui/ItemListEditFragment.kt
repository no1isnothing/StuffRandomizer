package com.example.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stuffrandomizer.R

/**
 * A simple [Fragment] subclass.
 * Use the [ItemListEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemListEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_list_edit, container, false)
    }

}