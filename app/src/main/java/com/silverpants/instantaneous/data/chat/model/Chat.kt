package com.silverpants.instantaneous.data.chat.model

data class Chat(val users: List<String>, val immediate: List<String>) {
    // by default set to zero, Indicates which user is my data
    var meUserId: String = ""
    private val userIndex: Int get() = users.indexOf(meUserId)
    private val anotherUserIndex: Int get() = if (userIndex == 0) 1 else 0

    fun getMyUserId(): String {
        return users[userIndex]
    }

    fun getMyUserImmediateMessage(): String {
        return immediate[userIndex]
    }

    fun getAnotherUserId(): String {
        return users[anotherUserIndex]
    }

    fun getAnotherUserImmediateMessage(): String {
        return immediate[anotherUserIndex]
    }
}