package com.silverpants.instantaneous.ui.search

import android.view.View
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.user.models.AnotherUser
import com.silverpants.instantaneous.databinding.ItemSearchUserBinding
import com.silverpants.instantaneous.misc.loadImageOrDefault
import com.xwray.groupie.viewbinding.BindableItem

class SearchItem(private val anotherUser: AnotherUser) : BindableItem<ItemSearchUserBinding>() {

    override fun initializeViewBinding(view: View): ItemSearchUserBinding {
        return ItemSearchUserBinding.bind(view)
    }

    override fun bind(viewBinding: ItemSearchUserBinding, position: Int) {
        viewBinding.tvSearchName.text = anotherUser.name
        viewBinding.tvSearchUserId.text = "@ ${anotherUser.userId}"
        loadImageOrDefault(viewBinding.civSearchDp, anotherUser.photoURL, R.drawable.ic_basketball)
    }

    override fun getLayout(): Int = R.layout.item_search_user
}