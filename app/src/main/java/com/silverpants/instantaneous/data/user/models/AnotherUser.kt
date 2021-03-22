package com.silverpants.instantaneous.data.user.models

import java.util.*

data class AnotherUser(
    val name: String,
    val userId: String,
    val isOnline: Boolean,
    val lastOnline: Date,
    val photoURL: String
)