package com.silverpants.instantaneous.domain.chat

import com.silverpants.instantaneous.data.chat.ChatRepository
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.FlowUseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatMessagesFlowCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Pair<String, String>, List<Message>>(dispatcher) {
    override fun execute(parameters: Pair<String, String>): Flow<Result<List<Message>>> {
        return chatRepository.getObservableChatMessages(parameters.first, parameters.second)
    }

}