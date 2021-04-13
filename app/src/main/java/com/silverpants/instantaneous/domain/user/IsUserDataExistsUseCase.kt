package com.silverpants.instantaneous.domain.user

import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.data.user.models.UserState
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class IsUserDataExistsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) :
    UseCase<String?, UserState>(dispatcher) {
    @ExperimentalCoroutinesApi
    override suspend fun execute(parameters: String?): UserState {
        return userRepository.isUserDataExists(parameters)
    }
}