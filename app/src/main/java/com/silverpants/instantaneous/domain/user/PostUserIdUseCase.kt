package com.silverpants.instantaneous.domain.user

import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.data.user.models.FirestoreUserInfo
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class PostUserIdUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Triple<String, String, String>, FirestoreUserInfo>(dispatcher) {
    @ExperimentalCoroutinesApi
    override suspend fun execute(parameters: Triple<String, String, String>): FirestoreUserInfo {
        return userRepository.postUserIdAndNumber(parameters.first, parameters.second, parameters.third)
    }
}