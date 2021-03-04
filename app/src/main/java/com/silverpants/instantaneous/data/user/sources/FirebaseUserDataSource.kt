package com.silverpants.instantaneous.data.user.sources

import com.google.firebase.auth.FirebaseAuth
import com.silverpants.instantaneous.data.user.models.FirebaseUserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseUserDataSource @Inject constructor(val auth: FirebaseAuth) {

    fun getBasicUserInfo(): Flow<FirebaseUserInfo> {
        return channelFlow<FirebaseAuth> {
            val authStateListener: ((FirebaseAuth) -> Unit) = { auth ->
                channel.offer(auth)
            }
            auth.addAuthStateListener(authStateListener)
            awaitClose { auth.removeAuthStateListener(authStateListener) }
        }.map { authState ->
            FirebaseUserInfo(authState.currentUser)
        }
    }

}