package com.wujie.android.voicedemo.recognization.offline

import android.app.Activity
import android.content.SharedPreferences
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.recognization.CommonRecogParams
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by wujie on 2018/8/10/010.
 *
 */
class OfflineRecogParams(context: Activity) : CommonRecogParams(context) {
    companion object {
        val TAG = "OfflineRecogParams"
    }

    override fun fetch(sp: SharedPreferences): Map<String, Any> {
        val map = super.fetch(sp) as HashMap
        map[SpeechConstant.DECODER] = 2
        map.remove(SpeechConstant.PID)
        return map
    }

    fun fetchOfflineParams(): Map<String, Any> {
        val map = java.util.HashMap<String, Any>()
        map[SpeechConstant.DECODER] = 2
        map[SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH] = "assets:///baidu_speech_grammar.bsg"
        map.putAll(fetchSlotDataParam())
        return map
    }

    fun fetchSlotDataParam(): Map<String, Any> {
        val map = java.util.HashMap<String, Any>()
        try {
            val json = JSONObject()
            json.put("name", JSONArray().put("妈妈").put("老伍"))
                    .put("appname", JSONArray().put("手百").put("度秘"))
            map[SpeechConstant.SLOT_DATA] = json
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return map
    }

}

