package com.silverpants.instantaneous.ui.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentMainChatBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.loadImageOrDefault
import com.xwray.groupie.GroupieAdapter
import java.text.SimpleDateFormat
import java.util.*

class MainChatFragment : Fragment(R.layout.fragment_main_chat) {

    private val chatViewModel: ChatViewModel by viewModels()
    private val args: MainChatFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMainChatBinding.bind(view)
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val adapter = GroupieAdapter()

        chatViewModel.anotherUser.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        loadImageOrDefault(
                            binding.civChatDp,
                            it.data.photoURL,
                            R.drawable.ic_basketball
                        )
                        binding.imChatOnline.visibility =
                            if (it.data.isOnline) View.VISIBLE else View.INVISIBLE
                        binding.tvChatLastSeen.text =
                            if (it.data.isOnline) "Online" else simpleDateFormat.format(it.data.lastOnline)
                    }
                    else -> {
                    }
                }
            }
        }
        chatViewModel.chatMessages.observe(viewLifecycleOwner) {
        }

    }
}