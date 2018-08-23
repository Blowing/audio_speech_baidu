package com.wujie.android.voicedemo.activity.mini

import android.os.Message
import android.util.Log
import android.view.View
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.activity.ActivityCommon
import com.wujie.android.voicedemo.activity.test.AlarmListener
import com.wujie.android.voicedemo.activity.test.SpeechTestCase
import com.wujie.android.voicedemo.recognization.RecogEventAdapter
import com.wujie.android.voicedemo.recognization.inputstream.InFileStream
import com.wujie.android.voicedemo.util.Logger
import org.json.JSONObject
import java.util.*

/**
 * Created by wujie on 2018/8/23/023.
 */
class ActivityMiniTest : ActivityCommon() {
    private val enableOffline = true // 测试离线命令词，需要改成true
    private var asr: EventManager? = null

    private var runningTestName = ""

    private var index = 0

    private var cases: ArrayList<SpeechTestCase>? = null

    // ========================TEST CASE ==============================


    private// defaultParams.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音。默认普通识别，识别出音频文件的第一句话。
    // defaultParams.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号
    // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
    // =========================
    // =========================
    val tests: ArrayList<SpeechTestCase>
        get() {
            val cases = ArrayList<SpeechTestCase>()

            val defaultParams = HashMap<String, Any>()
            defaultParams[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
            val params = HashMap(defaultParams)

            cases.add(SpeechTestCase("firstTest", "16k_test.pcm", params))
            val params2 = HashMap(defaultParams)
            cases.add(SpeechTestCase("secondTest", "luyin1.pcm", params2))

            return cases
        }

    init {
        descText = ("高级，批量测试音频文件， 具体定义在getTest()中\n"
                + "请先使用如‘在线识别’界面确认识别参数，先测试一个音频文件，成功后再批量进行\n"
                + "如果测试出问题，请将原始音频一起反馈\n"
                + "测试音频请放在 app/src/main/resources/com/baidu/android/voicedemo/test/ \n\n")
    }

    override fun initRecog() {
        Logger.setHandler(handler!!)
        asr = EventManagerFactory.create(this, "asr")
        val listener = AlarmListener(handler!!)
        asr!!.registerListener(RecogEventAdapter(listener))
        btn?.setOnClickListener({ start() })
        if (enableOffline) {
            loadOfflineEngine() // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    private fun start() {
        cases = tests
        begin()

    }

    private fun begin(): Boolean {
        if (index >= cases!!.size) {
            return false
        }
        val testCase = cases!![index]
        runningTestName = testCase.name
        val params = testCase.params as HashMap
        val filename = "com/baidu/android/voicedemo/test/" + testCase.fileName

        val `is` = this.javaClass.classLoader.getResourceAsStream(filename)
        if (`is` == null) {
            txtLog?.append("filename:$filename does  not exist")
            index++
            begin()
        } else {
            InFileStream.reset()
            InFileStream.setInputStream(`is`)
            params.put(SpeechConstant.IN_FILE,
                    "#com.baidu.android.voicedemo.recognization.inputstream.InFileStream.create16kStream()")
            Log.i(TAG, "file:$filename")
            val json = JSONObject(params).toString()
            Log.i(TAG, "$runningTestName ,$json")
            asr!!.send(SpeechConstant.ASR_START, json, null, 0, 0)
        }
        return true
    }

    override fun handleMsg(msg: Message?) {
        val what = msg?.what
        when (what) {
            901 -> msg.obj = runningTestName + ": success : " + msg.obj.toString()
            900 -> {
                msg.obj = "$runningTestName : finish and exit\n"
                index++
                val success = begin()
                if (!success) {
                    index = 0
                }
            }
            -801 -> msg.obj = runningTestName + " error:" + msg.obj.toString()
        }// index= 9999999; 立即停止

        super.handleMsg(msg)
    }

    override fun initView() {
        super.initView()
        setting?.visibility = View.INVISIBLE
    }

    private fun loadOfflineEngine() {
        val params = LinkedHashMap<String, Any>()
        params[SpeechConstant.DECODER] = 2
        params[SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH] = "assets://baidu_speech_grammar.bsg"
        asr!!.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, JSONObject(params).toString(), null, 0, 0)
    }

    private fun unloadOfflineEngine() {
        asr!!.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0) //
    }

    override fun onDestroy() {
        super.onDestroy()
        asr!!.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0)
        if (enableOffline) {
            unloadOfflineEngine() // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    companion object {

        private val TAG = "ActivityMiniTest"
    }
}
