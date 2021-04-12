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
) : UseCase<Triple<String, String, String>, Message>(dispatcher) {
    override suspend fun execute(parameters: Triple<String, String, String>): Message {
        return chatRepository.postSendersNewMessage(
            parameters.first,
            parameters.second,
            parameters.third,
        )
    }
}