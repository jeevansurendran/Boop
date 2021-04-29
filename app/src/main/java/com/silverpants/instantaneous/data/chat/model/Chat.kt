package com.silverpants.instantaneous.data.chat.model

data class Chat(
    var chatId: String = "",
    val users: List<String> = emptyList(),
    val immediate0: String = "",
    val immediate1: String = ""
) {
    // by default set to zero, Indicates which user is my data
    var sUserId: String = ""
    val sendersUserIndex: Int get() = users.indexOf(sUserId)
    val receiversUserIndex: Int get() = if (sendersUserIndex == 0) 1 else 0

    fun getSendersUserId(): String {
        return users[sendersUserIndex]
    }

    fun geSendersImmediateMessage(): String {
        return if (sendersUserIndex == 0) immediate0 else immediate1
    }

    fun getReceiversUserId(): String {
        return users[receiversUserIndex]
    }

    fun getReceiversImmediateMessage(): String {
        return if (sendersUserIndex == 0) immediate1 else immediate0
    }
}