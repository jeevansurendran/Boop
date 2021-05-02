package com.silverpants.instantaneous.domain.chat

import com.google.firebase.functions.FirebaseFunctions
import com.silverpants.instantaneous.data.chat.ChatRepository
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.di.MainDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class PostMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val functions: FirebaseFunctions,
    @MainDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Triple<String, String, Triple<String, String, Pair<Boolean, String>>>, Message>(
    dispatcher
) {
    override suspend fun execute(parameters: Triple<String, String, Triple<String, String, Pair<Boolean, String>>>): Message {
        if (!parameters.third.third.first) {
            functions.getHttpsCallable("notifyNewMessage").call(
                hashMapOf(
                    "name" to parameters.third.third.second,
                    "userId" to parameters.third.second,
                    "message" to parameters.second,
                    "chatId" to parameters.first
                )
            )
        }
        return chatRepository.postSendersNewMessage(
            parameters.first,
            parameters.second,
            parameters.third.first
        )
    }
}