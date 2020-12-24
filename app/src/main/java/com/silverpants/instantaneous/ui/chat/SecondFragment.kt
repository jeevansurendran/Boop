package com.silverpants.instantaneous.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.room.models.Room
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