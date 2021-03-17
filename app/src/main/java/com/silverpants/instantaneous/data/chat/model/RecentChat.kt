package com.silverpants.instantaneous.data.chat.model

import java.util.*

data class RecentChat(
    val name: String,
    val userId: String,
    val lastOnline: Date,
    val roomId: String,
)