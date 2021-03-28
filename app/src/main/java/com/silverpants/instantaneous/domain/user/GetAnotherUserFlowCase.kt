package com.silverpants.instantaneous.domain.user


import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.data.user.models.AnotherUser
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.FlowUseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnotherUserFlowCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) :
    FlowUseCase<String, AnotherUser>(dispatcher) {
    override fun execute(parameters: String): Flow<Result<AnotherUser>> {
        return userRepository.getObservableAnotherUserInfo(parameters)
    }
}
