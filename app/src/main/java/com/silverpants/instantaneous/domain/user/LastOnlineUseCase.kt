package com.silverpants.instantaneous.domain.user

import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class LastOnlineUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Pair<String, Boolean>, Unit>(dispatcher) {
    override suspend fun execute(parameters: Pair<String, Boolean>) {
        userRepository.setOnline(parameters.first, parameters.second)
    }
}