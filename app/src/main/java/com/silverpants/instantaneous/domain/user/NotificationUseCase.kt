package com.silverpants.instantaneous.domain.user

import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class NotificationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<String, Unit>(dispatcher) {
    override suspend fun execute(parameters: String) {
        when (val user = userRepository.getObservableUserInfo().first()) {
            is Result.Success -> {
                userRepository.setNotificationToken(user.data.userId, parameters)
            }
            else -> {
            }
        }
    }
}