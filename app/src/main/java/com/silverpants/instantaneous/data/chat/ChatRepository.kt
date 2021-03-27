package com.silverpants.instantaneous.data.chat

import com.silverpants.instantaneous.data.chat.model.Chat
import com.silverpants.instantaneous.data.chat.model.Messages
import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.data.chat.sources.ChatDataSource
import com.silverpants.instantaneous.data.chat.sources.RecentChatDataSource
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val recentChatDataSource: RecentChatDataSource,
    private val chatDataSource: ChatDataSource
) {

    fun getObservableRecentChat(userId: String): Flow<Result<List<RecentChat>>> {
        return recentChatDataSource.getObservableRecentChat(userId).map { Result.Success(it) }
    }

    fun getObservableChat(chatId: String, userId: String): Flow<Result<Chat>> {
        return chatDataSource.getObservableChat(chatId, userId).map { Result.Success(it) }
    }

    fun getObservableChatMessages(chatId: String, userId: String): Flow<Result<Messages>> {
        return chatDataSource.getObservableChatMessages(chatId, userId).map { Result.Success(it) }
    }
}