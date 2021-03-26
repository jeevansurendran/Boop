package com.silverpants.instantaneous.data.chat.model

import java.util.*

data class Message(
    val userId: String = "",
    val message: String = "",
    val timestamp: Date = Date(),
    val isMe: Boolean = false
)