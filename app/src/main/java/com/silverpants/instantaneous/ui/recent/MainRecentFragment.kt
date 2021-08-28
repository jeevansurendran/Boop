package com.silverpants.instantaneous.ui.recent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentMainRecentBinding
import com.silverpants.instantaneous.misc.*
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicInteger

@AndroidEntryPoint
class MainRecentFragment : Fragment(R.layout.fragment_main_recent), RecentChatOnClickListener {

    private val recentChatViewModel: RecentChatViewModel by viewModels()
    private var buttonClick: AtomicInteger = AtomicInteger(1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMainRecentBinding.bind(view)
        val rv = binding.rvRecentChat
        val adapter = GroupieAdapter()
        val fallback = RecentChatFallbackItem(object : RecentChatFallbackListener {
            override fun onInviteFromContact() {
                recentChatViewModel.shareApp()
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
        binding.civRecentProfile.setOnClickListener {
            if (buttonClick.getAndIncrement() == EASTER_EGG_CLICK_COUNT) {
                openEaster()
            }
        }
        recentChatViewModel.shareUserLiveData.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        val intent = Intent().apply {
                            type = "text/plain"
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_SUBJECT, SHARE_TITLE)
                            putExtra(Intent.EXTRA_TEXT, it.data)
                        }
                        startActivity(Intent.createChooser(intent, "Share App"))
                    }
                    else -> {
                        val intent = Intent().apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, it.data)
                        }
                        startActivity(Intent.createChooser(intent, "Share App"))
                    }
                }
            }
        }
    }

    private fun openSearch() {
        val action = MainRecentFragmentDirections.openSearch()
        findNavController().navigate(action)
    }

    private fun openEaster() {
        buttonClick.set(1)
        val action = MainRecentFragmentDirections.openEaster()
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