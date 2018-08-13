package com.wujie.android.voicedemo.recognization

import android.app.Activity
import android.content.SharedPreferences
import android.os.Environment
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.R
import com.wujie.android.voicedemo.util.FileUtil
import com.wujie.android.voicedemo.util.Logger

/**
 * Created by wujie on 2018/8/9/009.
 */
open class CommonRecogParams {

    companion object {
        val TAG = "CommonRecogParams"
    }

    protected var samplePath: String? = ""

    /**
     * 字符串格式的参数
     */
    protected var stringParams = arrayListOf<String>()

    /**
     * int格式的参数
     */
    protected var intParams = arrayListOf<String>()

    /**
     * bool格式的参数
     */
    protected var boolParams = arrayListOf<String>()

    constructor(context: Activity) {
        stringParams.addAll(arrayListOf(
                SpeechConstant.VAD,
                SpeechConstant.IN_FILE))
        intParams.addAll(arrayListOf(
                SpeechConstant.PID,
                SpeechConstant.VAD_ENDPOINT_TIMEOUT
        ))
        boolParams.addAll(arrayListOf(
                SpeechConstant.ACCEPT_AUDIO_DATA,
                SpeechConstant.ACCEPT_AUDIO_VOLUME
        ))
        initSamplePath(context)
    }

    private fun initSamplePath(context: Activity) {
        val sampleDir = "baiduASR"
        samplePath = Environment.getExternalStorageDirectory().toString()+"/$sampleDir"
        if(!FileUtil.makeDir(samplePath!!)) {
            samplePath = context.application.getExternalFilesDir(sampleDir).absolutePath
            if (!FileUtil.makeDir(samplePath!!)) {
                throw  RuntimeException("创建目录失败：$samplePath")
            }
        }
    }

    open fun fetch(sp: SharedPreferences): Map<String, Any> {
        val map = HashMap<String, Any>()
        if (sp.getBoolean("_tips_sound", false)) {
            map[SpeechConstant.SOUND_START] = R.raw.bdspeech_recognition_start
            map[SpeechConstant.SOUND_CANCEL] = R.raw.bdspeech_recognition_cancel
            map[SpeechConstant.SOUND_ERROR] = R.raw.bdspeech_recognition_error
            map[SpeechConstant.SOUND_SUCCESS] = R.raw.bdspeech_recognition_success
            map[SpeechConstant.SOUND_END] = R.raw.bdspeech_speech_end
        }

        if (sp.getBoolean("_outfile", false)) {
            map[SpeechConstant.ACCEPT_AUDIO_DATA] = true
            map[SpeechConstant.OUT_FILE] = "$samplePath/outfile.pcm"
            Logger.info(TAG, "语音录音文件将保存在：$samplePath/outfile.pcm")
        }
        return map
    }

    private fun parseParamArr(sp: SharedPreferences, map: Map<String, Any> ) {
        stringParams.forEach {
            if (sp.contains(it)) {
                val tmp = sp.getString(it, "").replace(",.*", "").trim()
                if ("" != tmp) {
                    map as HashMap<String, Any>
                    map[it] = tmp
                }
            }
        }

        intParams.forEach {
            if (sp.contains(it)) {
                val tmp = sp.getString(it, "").replace(",.*", "").trim()
                if ("" != tmp) {
                    map as HashMap<String, Any>
                    map[it] = tmp.toInt()
                }
            }
        }

        boolParams.forEach {
            if (sp.contains(it)) {
                val res = sp.getBoolean(it, false)
                if (res || it == SpeechConstant.ACCEPT_AUDIO_VOLUME) {
                    map as HashMap<String, Any>
                    map[it] = res
                }
            }
        }

    }

}