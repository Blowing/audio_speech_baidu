package com.wujie.android.voicedemo.wakeup

import com.baidu.speech.EventListener
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.control.ErrorTranslation
import com.wujie.android.voicedemo.util.Logger


/**
 * Created by wujie on 2018/8/14/014.
 */
class WakeupEventAdapter(private val listener: IWakeupListener) : EventListener {

    override fun onEvent(name: String?, params: String?, data: ByteArray?, offset: Int, length: Int) {
        Logger.info(TAG, "wakeup name:$name; params: $params")
        when(name) {
            SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS ->{
                val result = WakeUpResult.parseJson(name, params)
                if (result.hasError()) {
                    listener.onError(result.errorCode, ErrorTranslation.wakeupError(result
                            .errorCode), result)
                } else {
                    listener.onSuccess(result.word, result)
                }
            }
            SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR -> {
                val result = WakeUpResult.parseJson(name, params)
                if (result.hasError()) {
                    listener.onError(result.errorCode, ErrorTranslation.wakeupError(result
                            .errorCode), result)
                }
             }
            SpeechConstant.CALLBACK_EVENT_WAKEUP_STOPED -> listener.onStop()
            SpeechConstant.CALLBACK_EVENT_WAKEUP_AUDIO -> listener.onAsrAudio(data, offset, length)

        }
    }

    companion object {
        const val TAG = "WakeupEventAdapter"
    }
}