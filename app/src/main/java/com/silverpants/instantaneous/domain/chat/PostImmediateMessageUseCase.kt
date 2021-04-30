package com.silverpants.instantaneous.domain.chat

import com.silverpants.instantaneous.data.chat.ChatRepository
import com.silverpants.instantaneous.di.MainDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class PostImmediateMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @MainDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Triple<String, String, Int>, Unit>(dispatcher) {
    override suspend fun execute(parameters: Triple<String, String, Int>) {
        chatRepository.postSendersImmediateMessage(
            parameters.first,
            parameters.second,
            parameters.third,
        )
    }
}