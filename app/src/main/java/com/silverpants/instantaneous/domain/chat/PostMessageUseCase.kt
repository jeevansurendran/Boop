package com.silverpants.instantaneous.domain.chat

import com.silverpants.instantaneous.data.chat.ChatRepository
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.di.MainDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class PostMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @MainDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Triple<String, String, Pair<String, Int>>, Message>(dispatcher) {
    override suspend fun execute(parameters: Triple<String, String, Pair<String, Int>>): Message {
        chatRepository.postSendersImmediateMessage(
            parameters.first,
            parameters.second,
            parameters.third.second
        )
        return chatRepository.postSendersNewMessage(
            parameters.first,
            parameters.second,
            parameters.third.first
        )
    }
}