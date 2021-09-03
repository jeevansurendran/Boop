package com.silverpants.instantaneous.domain.user

import com.silverpants.instantaneous.data.chat.ChatRepository
import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.data.user.models.CreateUser
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<CreateUser, Unit>(dispatcher) {
    @ExperimentalCoroutinesApi
    override suspend fun execute(parameters: CreateUser) {
        userRepository.createUser(parameters)
        if (parameters.sharersUserId !== null) {
            chatRepository.getChatId(parameters.userId, parameters.sharersUserId)
        }
    }
}