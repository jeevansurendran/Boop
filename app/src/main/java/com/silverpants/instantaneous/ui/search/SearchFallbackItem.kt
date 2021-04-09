package com.silverpants.instantaneous.ui.search

import android.view.View
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FallbackSearchBinding
import com.xwray.groupie.viewbinding.BindableItem

class SearchFallbackItem : BindableItem<FallbackSearchBinding>() {

    override fun initializeViewBinding(view: View): FallbackSearchBinding {
        return FallbackSearchBinding.bind(view)
    }

    override fun getLayout(): Int = R.layout.fallback_search

    override fun bind(viewBinding: FallbackSearchBinding, position: Int) {
    }
}