package com.silverpants.instantaneous.data.chat

import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.data.chat.sources.RecentChatDataSource
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepository @Inject constructor(private val recentChatDataSource: RecentChatDataSource) {

    fun getObservableRecentChat(userId: String): Flow<Result<List<RecentChat>>> {
        return recentChatDataSource.getObservableRecentChat(userId).map { Result.Success(it) }
    }
}