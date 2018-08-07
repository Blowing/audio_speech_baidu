package com.wujie.android.voicedemo.control

import android.content.Context
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.wujie.android.voicedemo.recognization.IRecogListener
import com.wujie.android.voicedemo.recognization.RecogEventAdapter
import com.wujie.android.voicedemo.util.Logger
/**
 * Created by wujie on 2018/8/7/007.
 *
 */
class MyRecognizer(context: Context, private var eventListener: EventListener) {
    private lateinit var asr: EventManager


    companion object {
        private var isOfflinEnginLoaded = false
        private var isInited = false
        private val TAG = "MyRecognizer"
    }
    constructor(context: Context, recogListener: IRecogListener) : this(context, RecogEventAdapter(recogListener)) {}
    init {
        if(isInited) {
            Logger.error(TAG, "还未调用release()，请勿新建一个新类")
            throw RuntimeException("还未调用release()，请勿新建一个新类")
        }
        isInited = true
        asr = EventManagerFactory.create(context, "asr")
        asr.registerListener(eventListener)
    }




}

