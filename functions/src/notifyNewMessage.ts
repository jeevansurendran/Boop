/* eslint-disable require-jsdoc */
import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

export default
    async function (data: any, context: functions.https.CallableContext) {
    const userId = data["userId"];
    const text = data["message"];
    const chatId = data["chatId"]
    console.log(`New notification for ${userId} and message ${text}`)

    const anotherUserSnapshot = await admin.firestore()
        .doc(`/users/${userId}`).withConverter({
            toFirestore: (data: {
                notificationTokens: [string],
                isOnline: boolean, name: string
            }) => data,
            fromFirestore: (
                snap: FirebaseFirestore.QueryDocumentSnapshot) =>
                snap.data() as {
                    notificationTokens: [string],
                    isOnline: boolean, name: string
                },
        }).get();


    if (!anotherUserSnapshot.exists) {
        return;
    }

    const anotherUser = anotherUserSnapshot.data();
    console.log(`Details of the user ${JSON.stringify(anotherUser)}`)
    console.log(!anotherUser, anotherUser?.isOnline)
    if (!anotherUser) {
        return;
    }
    if (anotherUser.isOnline) {
        return;
    }
    const payload = {
        notification: {
            title: `${anotherUser.name}`,
            body: text,
            clickAction: "MainChatFragment",
        },
        data: {
            chatId,
        },
    };
    console.log(`message payload for FCM is${JSON.stringify(payload)} and FCM token are ${anotherUser.notificationTokens}`);
    const response =
        admin.messaging().sendToDevice(anotherUser.notificationTokens, payload);
    const stillNotificationTokens = anotherUser.notificationTokens;
    (await response).results.forEach((result, index) => {
        const error = result.error;
        if (error) {
            const failedToken = anotherUser.notificationTokens[index];
            console.log("there has been an error with this token", failedToken)
            if (error.code ===
                "messaging/invalid-registration-token" ||
                error.code === "messaging/registration-token-not-registered") {
                const failedIndex = stillNotificationTokens.indexOf(failedToken);
                if (failedIndex > -1) {
                    stillNotificationTokens.splice(failedIndex, 1);
                }
            }
        }
    });

    admin.firestore().doc(`users/${userId}`).update({
        notificationTokens: stillNotificationTokens,
    });
    return;

}
