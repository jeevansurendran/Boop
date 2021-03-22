package com.silverpants.instantaneous.data.chat.model

import java.util.*

data class Chat(
    val userId: String,
    val message: String,
    val timestamp: Date,
)