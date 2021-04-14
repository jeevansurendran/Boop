package com.silverpants.instantaneous.ui.recent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentMainRecentBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.hideKeyboard
import com.silverpants.instantaneous.misc.loadImageOrDefault
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainRecentFragment : Fragment(R.layout.fragment_main_recent), RecentChatOnClickListener {

    private val recentChatViewModel: RecentChatViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMainRecentBinding.bind(view)
        val rv = binding.rvRecentChat
        val adapter = GroupieAdapter()
        val fallback = RecentChatFallbackItem(object : RecentChatFallbackListener {
            override fun onInviteFromContact() {
                val intent = Intent().apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Download now")
                    putExtra(Intent.EXTRA_TEXT, "Try the app @ https://www.google.com")
                }
                startActivity(Intent.createChooser(intent, "Share App"))
            }

            override fun onSearchPeople() {
                openSearch()
            }

        })
        rv.adapter = adapter

        recentChatViewModel.recentChat.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                       adapter.replaceAll( it.data.takeIf { it.isNotEmpty() }?.map {
                           RecentChatItem(it, this)
                       } ?: listOf(fallback))
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

        binding.imRecentSearch.setOnClickListener {
            openSearch()
        }
    }

    fun openSearch() {
        val action = MainRecentFragmentDirections.openSearch()
        findNavController().navigate(action)
    }

    // What happens when the chat is clicked
    override fun onClick(chatId: String) {
        val action = MainRecentFragmentDirections.openChat(chatId)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideKeyboard()
    }
}