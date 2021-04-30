package com.silverpants.instantaneous.misc

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resumeWithException

// firebase
@ExperimentalCoroutinesApi
suspend fun <T> Task<T>.suspendAndWait(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result) {
                continuation.resumeWithException(it)
            }
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.resumeWithException(Exception("Firebase Task was cancelled"))
        }
    }

// toast
fun Fragment.toast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(requireContext(), message, length).show()
}

fun Activity.toast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}

//loadImageOrDefault
fun loadImageOrDefault(imageView: ImageView, url: String, @DrawableRes defaultDrawable: Int) {
    if (url.isNotEmpty()) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    } else {
        imageView.setImageResource(defaultDrawable)
    }
}

// hide keyboard
fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


// time
fun formatTimeDistance(eventTime: Date, currentTime: Date): String {
    val (first, value) = getTimeDifference(
        eventTime,
        currentTime
    )
    val suffix = if (value > 1) "s " else " "
    return when (first) {
        TimeUnit.DAYS -> String.format(Locale.getDefault(), "%d day%sago", value, suffix)
        TimeUnit.HOURS -> String.format(Locale.getDefault(), "%d hour%sago", value, suffix)
        TimeUnit.MINUTES -> String.format(Locale.getDefault(), "%d min%sago", value, suffix)
        else -> "Now"
    }
}

fun getTimeDifference(start: Date, end: Date): Pair<TimeUnit, Long> {
    val diffTime = end.time - start.time
    return getTimeDifference(diffTime)
}

fun getTimeDifference(diffTime: Long): Pair<TimeUnit, Long> {
    var diffTime = diffTime
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24
    val elapsedDays = diffTime / daysInMilli
    diffTime %= daysInMilli
    val elapsedHours = diffTime / hoursInMilli
    diffTime %= hoursInMilli
    val elapsedMinutes = diffTime / minutesInMilli
    diffTime %= minutesInMilli
    if (elapsedDays > 0) return TimeUnit.DAYS to elapsedDays
    if (elapsedHours > 0) return TimeUnit.HOURS to elapsedHours
    return if (elapsedMinutes > 0) TimeUnit.MINUTES to elapsedMinutes else TimeUnit.SECONDS to diffTime / secondsInMilli
}

fun EditText.onTextChanged(): ReceiveChannel<String> =
    Channel<String>(capacity = Channel.UNLIMITED).also { channel ->
        doOnTextChanged { text, start, before, count ->
            text?.toString().orEmpty().let(channel::offer)
        }
    }


fun <E> ReceiveChannel<E>.debounce(
    wait: Long = 50,
    context: CoroutineContext = Dispatchers.Default
): ReceiveChannel<E> = GlobalScope.produce(context) {
    var lastTimeout: Job? = null
    consumeEach {
        lastTimeout?.cancel()
        lastTimeout = launch {
            delay(wait)
            send(it)
        }
    }
    lastTimeout?.join()
}
