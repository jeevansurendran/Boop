package com.silverpants.instantaneous.ui.recent

import android.view.View
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.databinding.ItemRecentChatBinding
import com.silverpants.instantaneous.misc.formatTimeDistance
import com.silverpants.instantaneous.misc.loadImageOrDefault
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem
import java.util.*

class RecentChatItem(
    private val recentChat: RecentChat,
    private val listener: RecentChatOnClickListener
) : BindableItem<ItemRecentChatBinding>() {

    override fun bind(viewBinding: ItemRecentChatBinding, position: Int) {
        viewBinding.tvRecentName.text = recentChat.name
        viewBinding.tvRecentUserId.text = "@ ${recentChat.userId}"
        viewBinding.imRecentOnline.visibility =
            if (recentChat.isOnline) View.VISIBLE else View.INVISIBLE
        viewBinding.tvRecentLastSeen.text =
            if (recentChat.isOnline) "Online" else formatTimeDistance(
                recentChat.lastOnline,
                Calendar.getInstance().time
            )
        loadImageOrDefault(viewBinding.civRecentDp, recentChat.photoURL, R.drawable.ic_basketball)
        viewBinding.root.setOnClickListener {
            listener.onClick(recentChat.chatId)
        }
    }

    override fun getLayout(): Int = R.layout.item_recent_chat


    override fun initializeViewBinding(view: View): ItemRecentChatBinding {
        return ItemRecentChatBinding.bind(view)
    }
}