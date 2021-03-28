package com.silverpants.instantaneous.ui.recent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentMainRecentBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.loadImageOrDefault
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainRecentFragment : Fragment(R.layout.fragment_main_recent), RecentChatOnClickListener {

    private val recentChatViewModel: RecentChatViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMainRecentBinding.bind(view)
        val rv = binding.rvRecentChat
        val adapter = RecentChatAdapter(this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        recentChatViewModel.recentChat.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        adapter.setRecentChatList(it.data)
                    }
                    else -> {

                    }
                }
            }
        }
        recentChatViewModel.user.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        loadImageOrDefault(
                            binding.civRecentProfile,
                            it.data.photoURL,
                            R.drawable.ic_basketball
                        )

                    }
                    else -> {

                    }
                }
            }
        }

    }

    // What happens when the chat is clicked
    override fun onClick(chatId: String) {
        val action = MainRecentFragmentDirections.openChat(chatId)
        findNavController().navigate(action)
    }
}