package com.silverpants.instantaneous.ui.recent

import android.view.View
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FallbackRecentChatBinding
import com.xwray.groupie.viewbinding.BindableItem

class RecentChatFallbackItem(private val listener: RecentChatFallbackListener) :
    BindableItem<FallbackRecentChatBinding>() {

    override fun initializeViewBinding(view: View): FallbackRecentChatBinding {
        return FallbackRecentChatBinding.bind(view)
    }

    override fun getLayout(): Int = R.layout.fallback_recent_chat

    override fun bind(viewBinding: FallbackRecentChatBinding, position: Int) {
        viewBinding.btnRecentChatInvite.setOnClickListener {
            listener.onInviteFromContact()
        }
        viewBinding.btnRecentChatSearch.setOnClickListener {
            listener.onSearchPeople()
        }
    }
}