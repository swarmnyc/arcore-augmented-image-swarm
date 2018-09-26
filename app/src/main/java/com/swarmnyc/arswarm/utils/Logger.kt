package com.swarmnyc.arswarm.utils


import android.util.Log

object Logger {
    private const val TAG = "ar-swarm"

    fun d(message: String, throwable: Throwable? = null) {
        Log.d(TAG, message, throwable)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}