package com.wujie.android.voicedemo.wakeup

import android.os.Handler
import com.wujie.android.voicedemo.recognization.IStatus


/**
 * Created by wujie on 2018/8/14/014.
 *
 */
class RecogWakeupListener(private val handler: Handler) : SimpleWakeupListener(), IStatus {


    override fun onSuccess(word: String?, result: WakeUpResult) {
        super.onSuccess(word, result)
        handler.sendMessage(handler.obtainMessage(IStatus.STATUS_WAKEUP_SUCCESS))
    }

    companion object {
        const val TAG = "RecogWakeupListener"
    }

}