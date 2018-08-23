package com.wujie.android.voicedemo.activity.mini

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.R
import com.wujie.android.voicedemo.util.AutoCheck
import org.json.JSONObject
import java.util.*

/**
 * Created by wujie on 2018/8/23/023.
 */
class ActivityMiniRecog : AppCompatActivity(), EventListener {
    private var txtLog: TextView? = null
    private var txtResult: TextView? = null
    private var btn: Button? = null
    private var stopBtn: Button? =null

    private var asr: EventManager? = null

    private val logTime = true

    private val enableOffline = false // 测试离线命令词，需要改成true

    /**
     * 测试参数填在这里
     */
    private fun start() {
        txtLog?.text = ""
        val params = LinkedHashMap<String, Any>()
        var event: String? = null
        event = SpeechConstant.ASR_START // 替换成测试的event

        if (enableOffline) {
            params[SpeechConstant.DECODER] = 2
        }
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        // params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        // params.put(SpeechConstant.PROP ,20000);
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号
        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);

        // 复制此段可以自动检测错误
        AutoCheck(applicationContext, object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == 100) {
                    val autoCheck = msg.obj as AutoCheck
                    synchronized(autoCheck) {
                        val message = autoCheck.obtainErrorMessage() // autoCheck.obtainAllMessage();
                        txtLog?.append(message + "\n")
                        // Log.w("AutoCheckMessage", message);
                    }// 可以用下面一行替代，在logcat中查看代码
                }
            }
        }, enableOffline).checkAsr(params)
        var json: String? = null // 可以替换成自己的json
        json = JSONObject(params).toString() // 这里可以替换成你需要测试的json
        asr!!.send(event, json, null, 0, 0)
        printLog("输入参数：$json")
    }

    private fun stop() {
        printLog("停止识别：ASR_STOP")
        asr!!.send(SpeechConstant.ASR_STOP, null, null, 0, 0) //
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_mini)
        initView()
        initPermission()
        asr = EventManagerFactory.create(this, "asr")
        asr!!.registerListener(this) //  EventListener 中 onEvent方法
        btn?.setOnClickListener { start() }
        stopBtn?.setOnClickListener { stop() }
        if (enableOffline) {
            loadOfflineEngine() // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        asr!!.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0)
        if (enableOffline) {
            unloadOfflineEngine() // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    //   EventListener  回调方法
    override fun onEvent(name: String, params: String?, data: ByteArray?, offset: Int, length: Int) {
        var logTxt = "name: $name"


        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :$params"
        }
        if (name == SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL) {
            if (params!!.contains("\"nlu_result\"")) {
                if (length > 0 && data!!.isNotEmpty()) {
                    logTxt += ", 语义解析结果：" + String(data, offset, length)
                }
            }
        } else if (data != null) {
            logTxt += " ;data length=" + data.size
        }
        printLog(logTxt)
    }

    private fun printLog(text: String) {
        var text = text
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis()
        }
        text += "\n"
        Log.i(javaClass.name, text)
        txtLog?.append(text + "\n")
    }


    private fun initView() {
        txtResult = findViewById(R.id.txtResult)
        txtLog = findViewById(R.id.txtLog)
        btn = findViewById(R.id.btn)
        stopBtn = findViewById(R.id.btn_stop)
        txtLog?.text = DESC_TEXT + "\n"
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private fun initPermission() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val toApplyList = ArrayList<String>()

        for (perm in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm)
                // 进入到这里代表没有权限.

            }
        }
        val tmpList = arrayOfNulls<String>(toApplyList.size)
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toTypedArray(), 123)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    companion object {
        private const val DESC_TEXT = "精简版识别，带有SDK唤醒运行的最少代码，仅仅展示如何调用，\n" +
                "也可以用来反馈测试SDK输入参数及输出回调。\n" +
                "本示例需要自行根据文档填写参数，可以使用之前识别示例中的日志中的参数。\n" +
                "需要完整版请参见之前的识别示例。\n" +
                "需要测试离线命令词识别功能可以将本类中的enableOffline改成true，首次测试离线命令词请联网使用。之后请说出“打电话给张三”"
    }

}
