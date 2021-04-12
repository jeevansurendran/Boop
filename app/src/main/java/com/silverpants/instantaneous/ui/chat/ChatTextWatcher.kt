package com.silverpants.instantaneous.ui.chat

import android.text.Editable
import android.text.TextWatcher
import java.util.*

class ChatTextWatcher(val onFinish: (s: String) -> Unit) : TextWatcher {
    var timer = Timer()
    var DELAY: Long = 700

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        timer.cancel()
        timer.purge()
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.isNullOrEmpty()) {
            return
        }
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                onFinish(s.toString())
            }
        }, DELAY)
    }


}