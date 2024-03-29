package com.silverpants.instantaneous.domain.user


import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.data.user.models.User
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.FlowUseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservableUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) :
    FlowUseCase<Unit, User>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Result<User>> {
        return userRepository.getObservableUserInfo()
    }
}
