package com.gakk.noorlibrary.receiver

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.media.session.MediaButtonReceiver
import com.gakk.noorlibrary.service.AudioPlayerService
import com.gakk.noorlibrary.util.*

class AudioPlayerActionReceiver : MediaButtonReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        try {
            super.onReceive(context, intent)
        } catch (e: IllegalStateException) {
            Log.e("MediaButtonReceiver", "Super")
        }

        if (isNetworkConnected(context)){
            try {
                intent.let {
                    val type = intent.getStringExtra(ACTION_TYPE)
                    when (type) {
                        RESUME_AUDIO -> {
                            Log.i("RECEIVER", "$RESUME_AUDIO called")
                            AudioPlayerService.executePlayerCommand(RESUME_COMMAND)
                        }
                        PAUSE_AUDIO -> {
                            Log.i("RECEIVER", "$PAUSE_AUDIO called")
                            AudioPlayerService.executePlayerCommand(PAUSE_COMMAND)
                        }
                        NEXT_AUDIO -> {
                            Log.i("RECEIVER", "$NEXT_AUDIO called")
                            AudioPlayerService.executePlayerCommand(NEXT_COMMAND)
                        }
                        PREV_AUDIO -> {
                            Log.i("RECEIVER", "$PREV_AUDIO called")
                            AudioPlayerService.executePlayerCommand(PREV_COMMAND)
                        }
                        DISMISS_AUDIO_PLAYER_NOTIFICATION_SERVICE -> {
                            Log.i("RECEIVER", "$DISMISS_AUDIO_PLAYER_NOTIFICATION_SERVICE called")
                            AudioPlayerService.executePlayerCommand(DISMISS_COMMAND)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("MediaButtonReceiver", "Type")
            }
        }
    }
}