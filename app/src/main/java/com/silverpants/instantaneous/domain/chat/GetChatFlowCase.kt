package com.silverpants.instantaneous.domain.chat

import com.silverpants.instantaneous.data.chat.ChatRepository
import com.silverpants.instantaneous.data.chat.model.Chat
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.FlowUseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatFlowCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Pair<String, String>, Chat>(dispatcher) {
    override fun execute(parameters: Pair<String, String>): Flow<Result<Chat>> {
        return chatRepository.getObservableChat(parameters.first, parameters.second)
    }

}