package com.wujie.android.voicedemo.wakeup

import com.wujie.android.voicedemo.util.Logger

/**
 * Created by wujie on 2018/8/14/014.
 *
 */
open class SimpleWakeupListener : IWakeupListener {

    companion object {
        const val TAG = "SimpleWakeupListener"
    }
    override fun onSuccess(word: String?, result: WakeUpResult) {
        Logger.info(TAG, "唤醒成功，唤醒词：$word")
    }

    override fun onStop() {
        Logger.info(TAG, "唤醒词识别结束：")
    }

    override fun onError(errorCode: Int, errorMessage: String, result: WakeUpResult) {
        Logger.error(TAG, "唤醒错误$errorCode; 错误信息:$errorMessage; 原始返回 ${result.origalJson}")
    }

    override fun onAsrAudio(data: ByteArray?, offset: Int, length: Int) {
        Logger.info(TAG, "audio data: ${data?.size}")
    }
}