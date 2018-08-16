package com.wujie.android.voicedemo.control

import android.content.Context
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.recognization.IRecogListener
import com.wujie.android.voicedemo.recognization.RecogEventAdapter
import com.wujie.android.voicedemo.util.Logger
import org.json.JSONObject

/**
 * Created by wujie on 2018/8/7/007.
 *
 */
class MyRecognizer(context: Context, private var eventListener: EventListener) {

    private var asr: EventManager? = null

    companion object {
        private var isOfflinEnginLoaded = false
        private var isInited = false
        private val TAG = "MyRecognizer"
    }

    constructor(context: Context, recogListener: IRecogListener) : this(context, RecogEventAdapter(recogListener)) {}

    init {
        if (isInited) {
            Logger.error(TAG, "还未调用release()，请勿新建一个新类")
            throw RuntimeException("还未调用release()，请勿新建一个新类")
        }
        isInited = true
        asr = EventManagerFactory.create(context, "asr")
        asr!!.registerListener(eventListener)
    }


    fun loadOfflineEngine(params: Map<String, Any>) {
        val json = JSONObject(params)
        Logger.info("$TAG.Debug", "loadOfflineEngine params:$json")
        asr!!.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, json.toString(), null, 0, 0)
        isOfflinEnginLoaded = true
    }

    fun start(params: Map<String, Any>) {
        val json = JSONObject(params).toString()
        Logger.info("$TAG.Debug", "asr params(识别参数，反馈请带上此日志)：$json")
        asr!!.send(SpeechConstant.ASR_START, json, null, 0, 0)

    }

    fun stop() {
        Logger.info(TAG, "停止录音")
        asr!!.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0)
    }

    fun cancel() {
        Logger.info(TAG, "取消识别")
        asr!!.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0)

    }

    fun release() {
        if (asr == null) {
            return
        }
        cancel()
        if(isOfflinEnginLoaded) {
            asr!!.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0)
            isOfflinEnginLoaded = false
        }
        asr!!.unregisterListener(eventListener)
        asr = null
        isInited = false
    }

}

