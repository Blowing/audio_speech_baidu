package com.wujie.android.voicedemo.wakeup

import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.util.Logger
import org.json.JSONException
import org.json.JSONObject

class WakeUpResult {
    var name: String? = null
    var origalJson: String? = null
    var word: String? = null
    var desc: String? = null
    var errorCode: Int = 0

    fun hasError() = errorCode != ERROR_NONE

    companion object {
        var ERROR_NONE = 0
        const val TAG = "WakeUpResult"

        fun parseJson(name: String, jsonStr: String?): WakeUpResult {
            val result = WakeUpResult()
            result.origalJson = jsonStr

            try {
                val json = JSONObject(jsonStr)
                if (SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS == name &&
                        !result.hasError()) {
                    result.word = json.optString("word")
                }
                result.errorCode = json.optInt("error")
                result.desc = json.optString("desc")
            } catch (e: JSONException) {
                Logger.error(TAG, "Json parse error $jsonStr")
                e.printStackTrace()
            }

            return result
        }
    }
}
