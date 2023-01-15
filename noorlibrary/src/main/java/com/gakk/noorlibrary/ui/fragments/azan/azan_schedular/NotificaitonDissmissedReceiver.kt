package com.gakk.noorlibrary.ui.fragments.azan.azan_schedular

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gakk.noorlibrary.util.TAG_ALARM

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/4/2021, Tue
 */
class NotificaitonDissmissedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG_ALARM, "onReceive: dissmissed listener called")
        AzanPlayer.releaseMediaPlayer()
    }
}