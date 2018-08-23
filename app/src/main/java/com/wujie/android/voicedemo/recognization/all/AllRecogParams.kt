package com.wujie.android.voicedemo.recognization.all

import android.app.Activity
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.recognization.CommonRecogParams
import java.util.*

/**
 * Created by wujie on 2018/8/23/023.
 */
class AllRecogParams(context: Activity) : CommonRecogParams(context) {

    init {
        stringParams.addAll(Arrays.asList(
                SpeechConstant.NLU,
                "_language",
                "_model"))

        intParams.addAll(Arrays.asList(
                SpeechConstant.DECODER,
                SpeechConstant.PROP))

        boolParams.addAll(Arrays.asList(SpeechConstant.DISABLE_PUNCTUATION, "_nlu_online"))

        // copyOfflineResource(context);
    }

    companion object {


        private val TAG = "NluRecogParams"
    }


}