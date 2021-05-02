package com.silverpants.instantaneous.ui.chat

import android.view.View
import androidx.viewbinding.ViewBinding
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.databinding.ItemChatReceiveBinding
import com.silverpants.instantaneous.databinding.ItemChatSendBinding
import com.xwray.groupie.viewbinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

abstract class MessageItem<T : ViewBinding>(val message: Message) : BindableItem<T>() {
    fun isSameItem(message: Message): Boolean = this.message.messageId == message.messageId


}

class SendMessageItem(message: Message) : MessageItem<ItemChatSendBinding>(message) {


    override fun bind(viewBinding: ItemChatSendBinding, position: Int) {
        viewBinding.tvChatSendText.text = message.message
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        viewBinding.tvChatSendTime.text = timeFormat.format(message.timestamp)
    }

    override fun initializeViewBinding(view: View): ItemChatSendBinding {
        return ItemChatSendBinding.bind(view)
    }

    override fun getLayout(): Int = R.layout.item_chat_send

}

class ReceiveMessageItem(message: Message) : MessageItem<ItemChatReceiveBinding>(message) {

    override fun initializeViewBinding(view: View): ItemChatReceiveBinding {
        return ItemChatReceiveBinding.bind(view)
    }

    override fun bind(viewBinding: ItemChatReceiveBinding, position: Int) {
        viewBinding.tvChatReceiveText.text = message.message
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        viewBinding.tvChatReceiveTime.text = timeFormat.format(message.timestamp)
    }

    override fun getLayout(): Int = R.layout.item_chat_receive

}