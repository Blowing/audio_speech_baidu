package com.wujie.android.voicedemo.recognization

import android.app.Activity
import android.os.Environment
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.util.FileUtil

/**
 * Created by wujie on 2018/8/9/009.
 */
class CommonRecogParams {

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
}