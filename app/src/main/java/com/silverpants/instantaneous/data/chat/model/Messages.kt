package com.silverpants.instantaneous.data.chat.model

data class Messages(
    val messages: List<Message> = emptyList(),
    val messageChanges: List<MessageChange> = emptyList(),
) {
    class MessageChange(
        val type: MessageChange.Type,
        val message: Message,
        val oldIndex: Int,
        val newIndex: Int
    ) {
        enum class Type {
            ADDED,
            MODIFIED,
            REMOVED,
        }
    }
}