package com.silverpants.instantaneous.data.user.models

data class CreateUser(
    val userId: String,
    val uid: String,
    val number: String,
    val name: String,
    val sharersUserId: String? = null
)