Boop Android App
=================
Boop is a chat app built upon reach UI and latest technologies that simplifies any chat by removing the need for a send button. It helps in connecting with your friends faster, getting in touch with them and provide a casual type chat among your peers. 

This is the repository for Boop Android App written in Kotlin.

# Features

The app displays a list of users (usually whom you added as a friend) and lets you communicate with them via chat. The chat is a simplified chat supporting upto 50 messages at a time. Chat screen is completely based upon how other chats work upon except there is no send button.

When a user finishes typing and after a certain timeout the message is automatically sent to the user. This avoids the hassle and communication can be far faster. future integrations involve using ML models to figure out when a statement is complete, Store data in a local database to reduce fetching data all the time and provide notification channel for new messages.

---img---

# Development Environment

The app is wriiten entirely in Kotlin and uses the Gradle build system.

To build the app, first Make the project and build a release version of the app once done in Android Studio. A canary or a stable version of Android Studio 4.0 or newer is required and may be downloaded from [here](https://developer.android.com/studio).

# Architecture

The architecture is built around [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/).

We followed the recommendations laid out in the [Guide to App Architecture](https://developer.android.com/jetpack/docs/guide) when deciding on the architecture for the app. We kept logic away from Activities and Fragments and moved it to [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)s.
We observed data using [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) and did custom in code binding  to bind UI components in layouts to the app's data sources.

We used a Repository layer for handling data operations. Boop's data comes from a few different sources -  user data and chat data is stored in [Cloud Firestore](https://firebase.google.com/docs/firestore/) (either remotely or in a local cache for offline use), and the repository modules are responsible for handling all data operations and abstracting the data sources from the rest of the app (I liked using Firestore, but if we wanted to swap it out for a different data source in the future, our architecture allows us to do so in a clean way).

We implemented a lightweight domain layer, which sits between the data layer and the presentation layer, and handles discrete pieces of business logic off
the UI thread.

We used [Navigation component](https://developer.android.com/guide/navigation to simplify into a single Activity app.

Future integrations plan using [Room](https://developer.android.com/jetpack/androidx/releases/room) for storing chat data to avoid network load.

We use [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection. Hilt has proven to reduce upto 75% of the dependency injection code instead of Dagger, and also result in a 13% build time improvement.

## Firebase

The app makes considerable use of the following Firebase components:

-  [Cloud Firestore](https://firebase.google.com/docs/firestore/) is our source for all user data. Firestore gave us automatic sync and also seamlessly managed offline functionality for me.

- [Firebase Cloud Functions](https://firebase.google.com/docs/functions/) allowed me to run backend code.

- Firebase Kotlin extension (KTX) libraries to write more idiomatic Kotlin code when calling Firebase APIs. To learn more, read this [Firebase blog article](https://firebase.googleblog.com/2020/03/firebase-kotlin-ga.html) on the Firebase KTX libraries.

## Kotlin

Using Kotlin for the make the app was an easy choice: I like Kotlin's expressive, concise, and powerful syntax; I find that Kotlin's support for safety features for
nullability and immutability made our code more resilient; and I leveraged the enhanced functionality provided by [Android Ktx extensions](https://developer.android.com/kotlin/ktx). I preffered [coroutines](https://developer.android.com/kotlin/coroutines) for asynchronous tasks. Coroutines is the recommended way to do asynchronous programming in Kotlin.

# Copyright

    Copyright 2021, Jeevan Surendran, All rights reserved.
