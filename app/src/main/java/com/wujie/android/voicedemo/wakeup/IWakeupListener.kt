package com.wujie.android.voicedemo.wakeup

/**
 * Created by wujie on 2018/8/14/014.
 */
interface IWakeupListener {

    fun onSuccess(word: String?, result: WakeUpResult)

    fun onStop()

    fun onError(errorCode: Int, errorMessage: String, result: WakeUpResult)

    fun onAsrAudio(data: ByteArray?, offset: Int, length: Int)
}