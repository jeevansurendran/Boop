package com.silverpants.instantaneous.domain.user

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class NotificationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val crashlytics: FirebaseCrashlytics
) : UseCase<String, Unit>(dispatcher) {
    override suspend fun execute(parameters: String) {
        try {
            when (val user = userRepository.getObservableUserInfo().first()) {
                is Result.Success -> {
                    userRepository.setNotificationToken(user.data.userId, parameters)
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            crashlytics.recordException(e)
        }
    }
}