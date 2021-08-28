package com.silverpants.instantaneous.misc

import android.content.Intent
import android.net.Uri

object Intents {

    fun openPlayStoreIntent() =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=com.silverpants.instantaneous")
        )
}