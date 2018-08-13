package com.wujie.android.voicedemo.recognization.online

import android.app.Activity
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.recognization.CommonRecogParams

/**
 * Created by wujie on 2018/8/10/010.
 */
class OnlineRecogParams(context: Activity) : CommonRecogParams(context) {
    init {
        stringParams.addAll(arrayListOf("_language", "_model"))

        intParams.addAll(arrayListOf(SpeechConstant.PROP))

        boolParams.addAll(arrayListOf(SpeechConstant.DISABLE_PUNCTUATION))
    }

    companion object {
        val TAG = "OnlineRecogParams"
    }
}