package com.silverpants.instantaneous.domain.chat

import com.silverpants.instantaneous.data.chat.ChatRepository
import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.di.MainDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetChatIdUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    @MainDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Pair<String, String>, String>(dispatcher) {
    override suspend fun execute(parameters: Pair<String, String>): String {
        return chatRepository.getChatId(parameters.first, parameters.second)
    }
}