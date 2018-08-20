package com.wujie.android.voicedemo.control

import android.content.Context
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.util.Logger
import com.wujie.android.voicedemo.wakeup.IWakeupListener
import com.wujie.android.voicedemo.wakeup.WakeupEventAdapter
import org.json.JSONObject


/**
 * Created by wujie on 2018/8/16/016.
 *
 */
class MyWakeup( context: Context, private var eventListener: EventListener ? = null) {

    companion object {
        var isInited = false
        const val TAG = "MyWakeup"
    }

    private var wp: EventManager ?= null


    init {
        if (isInited) {
            Logger.info(TAG, "还未调用release()，请勿新建一个新类")
            throw RuntimeException("还未调用release(), 请勿新建一个新类")
        }
        isInited = true
        wp = EventManagerFactory.create(context, "wp")
        wp!!.registerListener(eventListener)
    }

    constructor(context: Context, eventListener: IWakeupListener) :this(context,
            WakeupEventAdapter(eventListener))

    fun start(params: Map<String, Any?>) {
        val json = JSONObject(params).toString()
        Logger.info("$TAG.Debug", "wakeup params(反馈请带上此行日志):$json")
        wp!!.send(SpeechConstant.WAKEUP_START, json,null, 0, 0)
    }


    fun stop() {
        Logger.info(TAG, "唤醒结束")
        wp!!.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0)
    }

    fun release() {
        stop()
        wp!!.unregisterListener(eventListener)
        wp = null
        isInited = false
    }
}