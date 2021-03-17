package com.silverpants.instantaneous.ui.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.databinding.ItemRecentChatBinding

class RecentChatAdapter(private val list: List<RecentChat>) :
    RecyclerView.Adapter<RecentChatAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentChatBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(list[position])
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val item: ItemRecentChatBinding) :
        RecyclerView.ViewHolder(item.root) {

        fun setData(recentChat: RecentChat) {
            item.tvRecentLastSeen.text = recentChat.lastOnline.toString()
        }

    }
}