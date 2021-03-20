package com.silverpants.instantaneous.domain.chat

import com.silverpants.instantaneous.data.chat.ChatRepository
import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.FlowUseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentChatFlowUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<String, List<RecentChat>>(dispatcher) {
    override fun execute(parameters: String): Flow<Result<List<RecentChat>>> {
        return chatRepository.getObservableRecentChat(parameters)
    }

}