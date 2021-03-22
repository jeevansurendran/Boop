package com.silverpants.instantaneous.data.chat.model

import java.util.*

data class RecentChat(
    val name: String,
    val userId: String,
    val lastOnline: Date,
    val isOnline: Boolean,
    val chatId: String,
    val photoURL: String
) {
    companion object {
        fun getDefault() = RecentChat("", "", Date(), false, "", "")
    }
}