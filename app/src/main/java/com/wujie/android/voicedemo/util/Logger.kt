package com.wujie.android.voicedemo.util

import android.os.Handler
import android.os.Message
import android.util.Log

/**
 * Created by wujie on 2018/8/7/007.
 */
object Logger {

    private val TAG = "Logger"

    private val INFO = "INFO"

    private val ERROR = "ERROR"

    private var ENABLE = true

    private var handler: Handler? = null

    fun info(message: String){
        info(TAG, message)
    }

    fun info(tag: String, message: String) {
        log(INFO, tag, message)
    }

    fun error(message: String) {
        error(TAG, message)
    }

    fun error(tag: String, message: String) {
        log(ERROR, tag, message)
    }

    fun setHandler(handler: Handler) {
        Logger.handler = handler
    }

    private fun log(level: String, tag: String, message: String) {
        if (!ENABLE) return

        if (level == INFO)
            Log.i(tag, message)
        else if (level == ERROR)
            Log.e(tag, message)

        if (handler != null) {
            val msg = Message.obtain()
            msg.obj = "[$level]$message\n"
            handler!!.sendMessage(msg)
        }
    }

    fun setEnable(isEnable: Boolean) {
        ENABLE = isEnable
    }
}