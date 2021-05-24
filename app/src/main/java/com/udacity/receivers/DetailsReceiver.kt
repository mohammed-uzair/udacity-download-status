package com.udacity.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DetailsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //Start the details activity
        if (context != null && intent != null) {
            context.startActivity(intent)
        }
    }
}