package com.silverpants.instantaneous.data.user.sources

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import javax.inject.Inject

class FirebaseUserDataSource @Inject constructor(val auth: FirebaseAuth) {
    fun getObservableFirebaseUser(): Flow<FirebaseUser?> {
        return channelFlow {
            val authStateListener: ((FirebaseAuth) -> Unit) = { auth ->
                if (isActive) {
                    auth.currentUser.let { channel.offer(it) }
                }
            }
            auth.addAuthStateListener(authStateListener)
            awaitClose {
                auth.removeAuthStateListener(authStateListener)
            }
        }
    }
}