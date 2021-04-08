package com.silverpants.instantaneous.data.user.models

import java.util.*

data class AnotherUser(
    val name: String = "",
    val userId: String = "",
    val isOnline: Boolean = false,
    val lastOnline: Date = Calendar.getInstance().time,
    val photoURL: String = ""
)