package com.example.morningbreak

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class HomeWatcher(var context: Context) {
    lateinit var mFilter: IntentFilter
    lateinit var mHomePressed: OnHomePressedListener
    lateinit var mReciever: InnerReceiver

    inner class InnerReceiver : BroadcastReceiver() {

        val SYSTEM_DIALOG_REASON_KEY = "reason"
        val SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions"
        val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"

        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            if (action != null && action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = p1.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                reason?.let {
                    mHomePressed?.let {
                        if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) {
                            mHomePressed.onHomePressed()
                        } else if (reason == SYSTEM_DIALOG_REASON_RECENT_APPS) {
                            mHomePressed.onHomeLongPpressed()
                        }
                    }
                }
            }
        }

    }

    operator fun invoke(context: Context) {
        this.context = context
        mFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    }

    fun setHomePressedListener(mHomePressedListener: OnHomePressedListener) {
        mHomePressed = mHomePressedListener
        mReciever = InnerReceiver()
    }

    fun startWatch() {
        mFilter = IntentFilter()
        mReciever?.let {
            context.registerReceiver(mReciever, mFilter)
        }
    }

    private fun stopWatch() {
        mReciever?.let {
            context.unregisterReceiver(mReciever)
        }
    }

    interface OnHomePressedListener {
        fun onHomePressed()
        fun onHomeLongPpressed()
    }
}