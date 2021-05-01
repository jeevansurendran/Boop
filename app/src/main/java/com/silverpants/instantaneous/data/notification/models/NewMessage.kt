package com.silverpants.instantaneous.data.notification.models

import java.util.*

data class NewMessage(
    val time: Date = Calendar.getInstance().time,
    val chatId: String = "",
    val name: String = "",
    val message: String = ""
)