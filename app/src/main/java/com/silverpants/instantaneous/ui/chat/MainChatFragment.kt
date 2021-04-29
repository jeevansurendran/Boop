package com.silverpants.instantaneous.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.databinding.FragmentMainChatBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.formatTimeDistance
import com.silverpants.instantaneous.misc.hideKeyboard
import com.silverpants.instantaneous.misc.loadImageOrDefault
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.viewbinding.BindableItem
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainChatFragment : Fragment(R.layout.fragment_main_chat) {

    private val chatViewModel: ChatViewModel by viewModels()
    private val args: MainChatFragmentArgs by navArgs()
    private val adapter = GroupieAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val chatBinding = FragmentMainChatBinding.bind(view)
        val rvChat = chatBinding.inclMainChat.rvChat
        val etChatNew: EditText = chatBinding.inclMainChat.inclMainChatNew.etChatNew

        rvChat.adapter = adapter
        rvChat.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                rvChat.postDelayed({
                    rvChat.smoothScrollToPosition(
                        if (adapter.itemCount - 1 < 0) 0 else adapter.itemCount - 1
                    )
                }, 100)
            }
        }
        etChatNew.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                postMessage(etChatNew, v.text.toString())
            }
            true
        }

//        etChatNew.addTextChangedListener(ChatTextWatcher {
//            requireActivity().runOnUiThread {
//                postMessage(etChatNew, it)
//            }
//        })

        etChatNew

        chatBinding.inclMainChat.inclMainChatNew.root.setOnClickListener {
            etChatNew.requestFocus()
            val imm: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etChatNew, InputMethodManager.SHOW_IMPLICIT)
            rvChat.scrollToPosition(if (adapter.itemCount - 1 < 0) 0 else adapter.itemCount - 1)
        }
        chatBinding.imChatBack.setOnClickListener {
            findNavController().popBackStack()
        }

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
                            if (it.data.isOnline) "Online" else formatTimeDistance(
                                it.data.lastOnline,
                                Calendar.getInstance().time
                            )
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
                        val textView =
                            chatBinding.inclMainChat.inclMainChatImmediate.tvChatImmediateText
                        textView.text = it.data.getReceiversImmediateMessage()
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
                        adapter.replaceAll(it.data.messages.map {
                            parseChatItem(it)
                        })
                        rvChat.scrollToPosition(if (adapter.itemCount - 1 < 0) 0 else adapter.itemCount - 1)
                    }
                    else -> {

                    }
                }
            }
        }
        chatViewModel.chatResult.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        adapter.add(SendMessageItem(it.data))
                        rvChat.scrollToPosition(if (adapter.itemCount - 1 < 0) 0 else adapter.itemCount - 1)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun postMessage(editText: EditText, message: String) {
        if (message.isBlank()) {
            return
        }
        editText.text.clear()
        chatViewModel.postImmediateMessage(message.trim())
    }

    private fun parseChatItem(message: Message): BindableItem<*> {
        return if (message.isMe) {
            SendMessageItem(message)
        } else {
            ReceiveMessageItem(message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideKeyboard()
    }
}

//                        for (messageChange in it.data.messageChanges) {
//                            when (messageChange.type) {
//                                Messages.MessageChange.Type.ADDED -> {
//                                    try {
//                                        //get the item and cast
//                                        val item =
//                                            adapter.getItem(messageChange.newIndex) as MessageItem<*>
//                                        // if its the same item then you dont need to change it so continue
//                                        if (item.isSameItem(messageChange.message)) {
//                                            continue
//                                        } else {
//                                            // since its different  add new item at that position and shift everything else
//                                            adapter.add(
//                                                messageChange.newIndex,
//                                                parseChatItem(message = messageChange.message)
//                                            )
//                                            rvChat.scrollToPosition(if (adapter.itemCount - 1 < 0) 0 else adapter.itemCount - 1)
//                                        }
//                                    } catch (e: Exception) {
//                                        // the item that you looked for was out of bound and hence add that item to that position
//                                        adapter.add(
//                                            messageChange.newIndex,
//                                            parseChatItem(message = messageChange.message)
//                                        )
//                                        rvChat.scrollToPosition(if (adapter.itemCount - 1 < 0) 0 else adapter.itemCount - 1)
//                                    }
//
//                                }
//                                Messages.MessageChange.Type.REMOVED -> {
//                                    val removedItem = adapter.getItem(messageChange.oldIndex)
//                                    adapter.remove(removedItem)
//                                }
//                                Messages.MessageChange.Type.MODIFIED -> {
//                                    // NOT YET
//                                }
//                            }
//                        }