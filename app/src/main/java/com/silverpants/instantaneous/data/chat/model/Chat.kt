package com.silverpants.instantaneous.data.chat.model

data class Chat(
    var chatId: String = "",
    val users: List<String> = emptyList(),
) {
    // by default set to zero, Indicates which user is my data
    var sUserId: String = ""
    val sendersUserIndex: Int get() = users.indexOf(sUserId)
    private val receiversUserIndex: Int get() = if (sendersUserIndex == 0) 1 else 0

    fun getSendersUserId(): String {
        return users[sendersUserIndex]
    }

    fun getReceiversUserId(): String {
        return users[receiversUserIndex]
    }
}