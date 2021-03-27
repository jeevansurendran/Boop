package com.silverpants.instantaneous.data.chat.model

import java.util.*

data class Message(
    var messageId: String = "",
    val userId: String = "",
    val message: String = "",
    val timestamp: Date = Date(),
    var isMe: Boolean = false
)