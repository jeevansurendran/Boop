package com.silverpants.instantaneous.domain.user

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.data.user.models.UserState
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import com.silverpants.instantaneous.misc.suspendAndWait
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class VerifyUserExistsUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) :
    UseCase<AuthCredential, UserState>(dispatcher) {
    @ExperimentalCoroutinesApi
    override suspend fun execute(parameters: AuthCredential): UserState {
        val result = auth.signInWithCredential(parameters).suspendAndWait()
        return userRepository.isUserDataExists(result.user?.uid)
    }
}