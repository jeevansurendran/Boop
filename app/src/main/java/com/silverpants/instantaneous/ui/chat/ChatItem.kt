package com.silverpants.instantaneous.ui.chat

import android.view.View
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.databinding.ItemChatReceiveBinding
import com.silverpants.instantaneous.databinding.ItemChatSendBinding
import com.xwray.groupie.viewbinding.BindableItem

class SendMessageItem(private val message: Message) : BindableItem<ItemChatSendBinding>() {


    override fun bind(viewBinding: ItemChatSendBinding, position: Int) {
        viewBinding.tvChatSendText.text = message.message
    }

    override fun initializeViewBinding(view: View): ItemChatSendBinding {
        return ItemChatSendBinding.bind(view)
    }

    override fun getLayout(): Int = R.layout.item_chat_send
}

class ReceiveMessageItem(private val message: Message) : BindableItem<ItemChatReceiveBinding>() {

    override fun initializeViewBinding(view: View): ItemChatReceiveBinding {
        return ItemChatReceiveBinding.bind(view)
    }

    override fun bind(viewBinding: ItemChatReceiveBinding, position: Int) {
        viewBinding.tvChatReceiveText.text = message.message
    }

    override fun getLayout(): Int = R.layout.item_chat_receive
}