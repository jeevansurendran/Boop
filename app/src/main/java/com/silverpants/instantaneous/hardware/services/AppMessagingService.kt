package com.silverpants.instantaneous.hardware.services

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.silverpants.instantaneous.domain.user.NotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationUseCase: NotificationUseCase

    @Inject
    lateinit var firebaseCrashlytics: FirebaseCrashlytics

    lateinit var updateNotificationTokenJob: Job

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateNotificationTokenJob = GlobalScope.launch {
            notificationUseCase(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            //TODO: show notification
            Timber.d("FCM Message received!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::updateNotificationTokenJob.isInitialized)
            if (!updateNotificationTokenJob.isCompleted) {
                try {
                    updateNotificationTokenJob.cancel()
                } catch (e: Exception) {
                    firebaseCrashlytics.recordException(e)
                }
            }
    }

}