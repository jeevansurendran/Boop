package com.silverpants.instantaneous.domain.user

import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Triple<String, String, Pair<String, String>>, Unit>(dispatcher) {
    @ExperimentalCoroutinesApi
    override suspend fun execute(parameters: Triple<String, String, Pair<String, String>>) {
        return userRepository.createUser(
            parameters.first,
            parameters.second,
            parameters.third.first,
            parameters.third.second
        )
    }
}