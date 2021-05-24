package com.udacity.util

import android.app.Activity
import android.view.Gravity
import android.widget.Toast

object Util {
    fun Activity.toast(message : String = ""){
        Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
            setGravity(Gravity.CENTER, 0, 0)
        }.show()
    }
}