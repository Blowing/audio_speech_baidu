package com.wujie.android.voicedemo.recognization.nlu

import android.app.Activity
import android.content.SharedPreferences
import com.baidu.speech.asr.SpeechConstant
import com.baidu.speech.asr.SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH
import com.wujie.android.voicedemo.recognization.CommonRecogParams
import com.wujie.android.voicedemo.recognization.offline.OfflineRecogParams

/**
 * Created by wujie on 2018/8/14/014.
 */
class NluRecogParams(context: Activity) : CommonRecogParams(context) {
    init {
        stringParams.addAll(arrayOf(SpeechConstant.NLU))
        intParams.addAll(arrayOf(SpeechConstant.DECODER, SpeechConstant.PROP))
        boolParams.addAll(arrayOf("_nlu_online"))
    }

    override fun fetch(sp: SharedPreferences) : Map<String, Any> {

        val map = super.fetch(sp) as HashMap
        if (sp.getBoolean("_grammar", false)) {
            val offlinParams = OfflineRecogParams.fetchOfflineParams()

            map[ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH] =
                    offlinParams[ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH]?: -1
        }
        if (sp.getBoolean("_slot_data", false)) {
            map.putAll(OfflineRecogParams.fetchSlotDataParam())
        }
        if (sp.getBoolean("_nlu_online", false)) {
            map["_model"] = "search"
        }
        return map

    }

}