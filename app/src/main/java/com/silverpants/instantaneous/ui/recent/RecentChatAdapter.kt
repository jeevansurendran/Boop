package com.silverpants.instantaneous.ui.recent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.databinding.ItemRecentChatBinding
import com.silverpants.instantaneous.misc.loadImageOrDefault
import java.text.SimpleDateFormat
import java.util.*

class RecentChatAdapter :
    RecyclerView.Adapter<RecentChatAdapter.ViewHolder>() {


    private var list = mutableListOf<RecentChat>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRecentChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun setRecentChatList(list: List<RecentChat>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val item: ItemRecentChatBinding) :
        RecyclerView.ViewHolder(item.root) {

        fun setData(recentChat: RecentChat) {
            item.tvRecentName.text = recentChat.name
            item.tvRecentUserId.text = "@ ${recentChat.userId}"
            val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            item.tvRecentLastSeen.text =
                if (recentChat.isOnline) "Online" else simpleDateFormat.format(recentChat.lastOnline)
            toggleChatOnline(recentChat.isOnline)
            loadImageOrDefault(item.civRecentDp, recentChat.photoURL, R.drawable.ic_basketball)
        }

        private fun toggleChatOnline(isOnline: Boolean) {
            item.imRecentOnline.visibility = if (isOnline) View.VISIBLE else View.INVISIBLE
        }

    }
}