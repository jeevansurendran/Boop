package com.silverpants.instantaneous.domain.user


import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.data.user.models.AnotherUser
import com.silverpants.instantaneous.di.MainDispatcher
import com.silverpants.instantaneous.domain.FlowUseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservableUserSearchCase @Inject constructor(
        private val userRepository: UserRepository,
        @MainDispatcher dispatcher: CoroutineDispatcher
) :
        FlowUseCase<Flow<String>, List<AnotherUser>>(dispatcher) {
        override fun execute(parameters: Flow<String>): Flow<Result<List<AnotherUser>>> {
                return userRepository.getUserSearch(parameters)
        }
}
