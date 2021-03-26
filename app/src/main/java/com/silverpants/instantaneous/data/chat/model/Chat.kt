package com.silverpants.instantaneous.data.chat.model

data class Chat(
    var chatId: String = "",
    val users: List<String> = emptyList(),
    val immediate: List<String> = emptyList()
) {
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