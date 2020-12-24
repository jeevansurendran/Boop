package com.silverpants.instantaneous.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.misc.USER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val chatViewModel: RoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userID = intent.getStringExtra(KEY_USER_ID)
        findNavController(R.id.nav_host_fragment).setGraph(
            R.navigation.nav_graph, bundleOf(
            "userID" to userID))
        chatViewModel.setRoomID("QHchQNGBEfojqvBT2kl6")
    }

    companion object {
        private const val KEY_USER_ID = "user.id"

        fun withUserID(context: Context, userID: USER) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(KEY_USER_ID, userID.uid)
            }
    }
}
