package com.silverpants.instantaneous.ui.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentSecondBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class SecondFragment : Fragment(R.layout.fragment_second) {

    private val roomViewModel: RoomViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSecondBinding.bind(view)

        roomViewModel.data.observe(viewLifecycleOwner, {
            binding.textviewSecond.text =
                "This is message1: ${it.message1} and this is message2: ${it.message2}"
        })
    }
}