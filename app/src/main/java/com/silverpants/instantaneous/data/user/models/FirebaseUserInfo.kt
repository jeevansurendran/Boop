package com.silverpants.instantaneous.data.user.models

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo

open class FirebaseUserInfo(
    private val firebaseUser: FirebaseUser?
) {

    fun isSignedIn(): Boolean = firebaseUser != null

    fun getEmail(): String? = firebaseUser?.email

    fun getProviderData(): MutableList<out UserInfo>? = firebaseUser?.providerData

    fun isAnonymous(): Boolean? = firebaseUser?.isAnonymous

    fun getPhoneNumber(): String? = firebaseUser?.phoneNumber

    fun getUid(): String? = firebaseUser?.uid

    fun isEmailVerified(): Boolean? = firebaseUser?.isEmailVerified

    fun getDisplayName(): String? = firebaseUser?.displayName

    fun getPhotoUrl(): Uri? = firebaseUser?.photoUrl

    fun getProviderId(): String? = firebaseUser?.providerId

    fun getLastSignInTimestamp(): Long? = firebaseUser?.metadata?.lastSignInTimestamp

    fun getCreationTimestamp(): Long? = firebaseUser?.metadata?.creationTimestamp
}