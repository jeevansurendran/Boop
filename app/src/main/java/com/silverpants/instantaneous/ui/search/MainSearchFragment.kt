package com.silverpants.instantaneous.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentMainSearchBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainSearchFragment : Fragment(R.layout.fragment_main_search) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMainSearchBinding.bind(view)

        binding.imSearchClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}