package com.silverpants.instantaneous.data.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.silverpants.instantaneous.data.user.models.AnotherUser
import com.silverpants.instantaneous.data.user.models.FirebaseUserInfo
import com.silverpants.instantaneous.data.user.models.FirestoreUserInfo
import com.silverpants.instantaneous.data.user.models.User
import com.silverpants.instantaneous.data.user.sources.FirebaseUserDataSource
import com.silverpants.instantaneous.data.user.sources.FirestoreUserDataSource
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.suspendAndWait
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDataSource: FirebaseUserDataSource,
    private val firestoreUserDataSource: FirestoreUserDataSource,
    val auth: FirebaseAuth
) {

    suspend fun updateUserDisplayName(displayName: String) {
        val changeRequest = UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
        auth.currentUser?.updateProfile(changeRequest)?.suspendAndWait()
    }

    fun getBasicUserInfo(): Flow<Result<FirebaseUserInfo>> {
        return userDataSource.getObservableFirebaseUser().map { currentUser ->
            Result.Success(FirebaseUserInfo(currentUser))
        }
    }

    suspend fun postUserIdAndNumber(
        userId: String,
        uid: String,
        number: String
    ): FirestoreUserInfo {
        return firestoreUserDataSource.postUserIdAndNumber(userId, uid, number)
    }

    suspend fun isUserDataExists(uid: String?): Boolean {
        return firestoreUserDataSource.isUserDataExists(uid)
    }

    fun getObservableUserInfo(): Flow<Result<User>> {
        return firestoreUserDataSource.getUserInfo().map {
            Result.Success(it)
        }
    }

    fun getObservableAnotherUserInfo(userId: String): Flow<Result<AnotherUser>> {
        return firestoreUserDataSource.getAnotherUserInfo(userId).map { Result.Success(it) }
    }

    fun getUserSearch(query: Flow<String>): Flow<Result.Success<List<AnotherUser>>> {
        return firestoreUserDataSource.searchUsers(query).map { Result.Success(it) }
    }
}