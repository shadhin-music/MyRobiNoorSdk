package com.gakk.noorlibrary.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gakk.noorlibrary.ui.activity.MainActivity
import com.gakk.noorlibrary.util.ACTION_SHARE

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/4/2021, Tue
 */
class NotificationClickReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent!!.action

        if (action.equals(ACTION_SHARE, ignoreCase = true)) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(i)
        }
    }
}