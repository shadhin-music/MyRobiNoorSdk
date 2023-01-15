package com.gakk.noorlibrary.ui.fragments.azan.azan_schedular

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gakk.noorlibrary.ui.activity.MainActivity
import com.gakk.noorlibrary.util.ACTION_SHARE
import com.gakk.noorlibrary.util.TAG_ALARM

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/4/2021, Tue
 */
class NotificationClickReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG_ALARM, "share reciver invoked")

        AzanPlayer.releaseMediaPlayer()

        val action = intent!!.action

        if (action.equals(ACTION_SHARE, ignoreCase = true)) {
            Log.d(TAG_ALARM, "share reciver invoked MainActivity")
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(i)
        }
    }
}