package com.wujie.android.voicedemo.activity.mini

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.R
import org.json.JSONObject
import java.util.*

/**
 * Created by wujie on 2018/8/23/023.
 */
class ActivityMiniWakeUp : AppCompatActivity(), EventListener {
    private var txtLog: TextView? = null
    private var txtResult: TextView? = null
    private var btn: Button? = null
    private var stopBtn: Button? = null

    private var wakeup: EventManager? = null

    private val logTime = true

    /**
     * 测试参数填在这里
     */
    private fun start() {
        txtLog?.text = ""
        val params = TreeMap<String, Any>()

        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
        params[SpeechConstant.WP_WORDS_FILE] = "assets:///WakeUp.bin"
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        var json: String? = null // 这里可以替换成你需要测试的json
        json = JSONObject(params).toString()
        wakeup!!.send(SpeechConstant.WAKEUP_START, json, null, 0, 0)
        printLog("输入参数：$json")
    }

    private fun stop() {
        wakeup!!.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0) //
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_mini)
        initView()
        wakeup = EventManagerFactory.create(this, "wp")
        wakeup!!.registerListener(this) //  EventListener 中 onEvent方法
        btn?.setOnClickListener { start() }
        stopBtn?.setOnClickListener { stop() }
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeup!!.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0)
    }

    //   EventListener  回调方法
    override fun onEvent(name: String, params: String?, data: ByteArray?, offset: Int, length: Int) {
        var logTxt = "name: $name"
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :$params"
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

    companion object {
        private val DESC_TEXT = "精简版唤醒，带有SDK唤醒运行的最少代码，仅仅展示如何调用，\n" +
                "也可以用来反馈测试SDK输入参数及输出回调。\n" +
                "本示例需要自行根据文档填写参数，可以使用之前唤醒示例中的日志中的参数。\n" +
                "需要完整版请参见之前的唤醒示例。\n\n" +
                "唤醒词是纯离线功能，需要获取正式授权文件（与离线命令词的正式授权文件是同一个）。 第一次联网使用唤醒词功能自动获取正式授权文件。之后可以断网测试\n" +
                "请说“小度你好”或者 “百度一下”\n\n"
    }


}
