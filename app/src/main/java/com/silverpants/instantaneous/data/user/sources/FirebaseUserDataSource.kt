package com.silverpants.instantaneous.data.user.sources

import com.google.firebase.auth.FirebaseAuth
import com.silverpants.instantaneous.data.user.models.FirebaseUserInfo
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.Result.Success
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import javax.inject.Inject

class FirebaseUserDataSource @Inject constructor(val auth: FirebaseAuth) {

    @ExperimentalCoroutinesApi
    fun getBasicUserInfo(): Flow<Result<FirebaseUserInfo>> {
        return channelFlow<FirebaseAuth> {
            val authStateListener: ((FirebaseAuth) -> Unit) = { auth ->
                if (isActive) {
                    channel.offer(auth)
                }
            }
            auth.addAuthStateListener(authStateListener)
            awaitClose {
                auth.removeAuthStateListener(authStateListener)
            }
        }.map { authState ->
            Success(FirebaseUserInfo(authState.currentUser))
        }
    }

}