package com.silverpants.instantaneous.domain.share

import android.net.Uri
import com.silverpants.instantaneous.data.dynamiclinks.DynamicLinkSource
import com.silverpants.instantaneous.data.user.UserRepository
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.UseCase
import com.silverpants.instantaneous.misc.DEFAULT_SHARE_URL
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ShareAppUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dynamicLinkSource: DynamicLinkSource,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, String>(dispatcher) {
    override suspend fun execute(parameters: Unit): String {
        val user = userRepository.getBasicUserInfo().first()
        val uri = when (user) {
            is Result.Success -> {
                val url = Uri.Builder().apply {
                    scheme("https")
                    authority("silverpants.com")
                    appendPath("shares")
                    appendPath(user.data.getUid())
                }.build()
                dynamicLinkSource.createShareableLink(url)
            }
            else -> {
                DEFAULT_SHARE_URL
            }
        }
        return ("Hey, ${user.data?.getDisplayName()} shared an app with you.\n\n" +
                "Boop takes a unique twist on your chatting experience. " +
                "Made for conversation to stay in the moment and disappear like magic ✨ \n\n\n Try today: " + uri)
    }
}

