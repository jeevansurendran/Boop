package com.silverpants.instantaneous.ui.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.data.chat.model.Messages
import com.silverpants.instantaneous.databinding.FragmentMainChatBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.loadImageOrDefault
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.viewbinding.BindableItem
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainChatFragment : Fragment(R.layout.fragment_main_chat) {

    private val chatViewModel: ChatViewModel by viewModels()
    private val args: MainChatFragmentArgs by navArgs()
    private val adapter = GroupieAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val chatBinding = FragmentMainChatBinding.bind(view)
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        chatBinding.inclMainChat.rvChat.adapter = adapter

        chatViewModel.user.observe(viewLifecycleOwner) {
            //TODO clean this mess
            chatViewModel.setChatId(args.chatId)
        }
        chatViewModel.anotherUser.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        loadImageOrDefault(
                            chatBinding.civChatDp,
                            it.data.photoURL,
                            R.drawable.ic_basketball
                        )
                        chatBinding.imChatOnline.visibility =
                            if (it.data.isOnline) View.VISIBLE else View.INVISIBLE
                        chatBinding.tvChatLastSeen.text =
                            if (it.data.isOnline) "Online" else simpleDateFormat.format(it.data.lastOnline)
                        chatBinding.tvChatName.text = it.data.name
                        chatBinding.tvChatUserId.text = "@ ${it.data.userId}"
                    }
                    else -> {
                    }
                }
            }
        }
        chatViewModel.chat.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        val inclMainChatReceive = chatBinding.inclMainChat.inclMainChatReceive
                        if (it.data.getAnotherUserImmediateMessage().isEmpty()) {
                            inclMainChatReceive.root.visibility = View.GONE
                            inclMainChatReceive.tvChatReceiveText.text = ""
                            return@observe
                        }
                        inclMainChatReceive.root.visibility = View.VISIBLE
                        inclMainChatReceive.tvChatReceiveText.text =
                            it.data.getAnotherUserImmediateMessage()
                    }
                    else -> {

                    }
                }
            }
        }
        chatViewModel.chatMessages.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        for (messageChange in it.data.messageChanges) {
                            when (messageChange.type) {
                                Messages.MessageChange.Type.ADDED -> {
                                    try {
                                        //get the item and cast
                                        val item =
                                            adapter.getItem(messageChange.newIndex) as MessageItem<*>
                                        // if its the same item then you dont need to change it so continue
                                        val bruh = item.isSameItem(messageChange.message)
                                        if (item.isSameItem(messageChange.message)) {
                                            continue
                                        } else {
                                            // since its different  add new item at that position and shift everything else
                                            adapter.add(
                                                messageChange.newIndex,
                                                parseChatItem(message = messageChange.message)
                                            )
                                        }
                                    } catch (e: Exception) {
                                        // the item that you looked for was out of bound and hence add that item to that position
                                        adapter.add(
                                            messageChange.newIndex,
                                            parseChatItem(message = messageChange.message)
                                        )
                                    }

                                }
                                Messages.MessageChange.Type.REMOVED -> {
                                    val removedItem = adapter.getItem(messageChange.oldIndex)
                                    adapter.remove(removedItem)
                                }
                                Messages.MessageChange.Type.MODIFIED -> {
                                    // NOT YET
                                }
                            }
                        }

                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun parseChatItem(message: Message): BindableItem<*> {
        return if (message.isMe) {
            SendMessageItem(message)
        } else {
            ReceiveMessageItem(message)
        }
    }
}