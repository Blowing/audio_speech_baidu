package com.wujie.android.voicedemo.activity

import android.os.Message
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.control.MyRecognizer
import com.wujie.android.voicedemo.control.MyWakeup
import com.wujie.android.voicedemo.recognization.IStatus
import com.wujie.android.voicedemo.recognization.IStatus.Companion.STATUS_WAKEUP_SUCCESS
import com.wujie.android.voicedemo.recognization.MessageStatusRecogListener
import com.wujie.android.voicedemo.wakeup.RecogWakeupListener
import java.util.*

/**
 * Created by wujie on 2018/8/23/023.
 */
class ActivityWakeUpRecog : ActivityWakeUp(), IStatus {

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    private var myRecognizer: MyRecognizer? = null

    /**
     * 0: 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
     * >0 : 方案2： 唤醒词说完后，中间有停顿，然后接句子。推荐4个字 1500ms
     *
     *
     * backTrackInMs 最大 15000，即15s
     */
    private val backTrackInMs = 1500

    init {
        descText = ("请先单独测试唤醒词功能和在线识别功能。\n"
                + "唤醒后识别，即唤醒词识别成功后，立即在线识别。\n"
                + "根据用户说唤醒词之后有无停顿。共2种实现方式。\n"
                + "1. 您的场景需要唤醒词之后无停顿，用户一下子说出。那么可以使用录音回溯功能：连同唤醒词一起整句识别。\n"
                + "2. 您的场景引导唤醒词之后有短暂停顿。那么不做回溯，识别出唤醒词停顿后的句子\n"
                + "两个方案的优劣 ：方案1 中，整句回溯时间是经验值1.5s。 此外由于整句识别，可能唤醒词会识别成别的结果。\n"
                + " 方案2中， 如果用户不停顿，将可能造成首字或者首词的识别错误。\n"
                + "代码中通过backTrackInMs可以选择测试方案1还是方案2。\n\n"
                + "测试请说：百度一下【此处可以停顿】，今天天气真好")
    }


    override fun initRecog() {
        // 初始化识别引擎

        val recogListener = MessageStatusRecogListener(handler)
        // 改为 SimpleWakeupListener 后，不依赖handler，但将不会在UI界面上显示
        myRecognizer = MyRecognizer(this, recogListener)

        val listener = RecogWakeupListener(handler!!)
        myWakeup = MyWakeup(this, listener)

    }

    override fun handleMsg(msg: Message?) {
        super.handleMsg(msg)
        if (msg?.what == STATUS_WAKEUP_SUCCESS) {
            // 此处 开始正常识别流程
            val params = LinkedHashMap<String, Any>()
            params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
            params[SpeechConstant.VAD] = SpeechConstant.VAD_DNN
            // 如识别短句，不需要需要逗号，使用1536搜索模型。其它PID参数请看文档
            params[SpeechConstant.PID] = 1536
            params[SpeechConstant.AUDIO_MILLS] = System.currentTimeMillis() - backTrackInMs
            myRecognizer?.cancel()
            myRecognizer?.start(params)
        }
    }

    override fun stop() {
        super.stop()
        myRecognizer?.stop()
    }

    override fun onDestroy() {
        myRecognizer?.release()
        super.onDestroy()
    }

    companion object {
        private val TAG = "ActivityWakeUpRecog"
    }
}