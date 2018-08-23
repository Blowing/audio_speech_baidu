package com.wujie.android.voicedemo.activity

import android.view.View
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.R
import com.wujie.android.voicedemo.control.MyWakeup
import com.wujie.android.voicedemo.recognization.IStatus
import com.wujie.android.voicedemo.recognization.IStatus.Companion.STATUS_WAITING_READY
import com.wujie.android.voicedemo.wakeup.RecogWakeupListener
import java.util.*

/**
 * Created by wujie on 2018/8/23/023.
 */
open class ActivityWakeUp : ActivityCommon(), IStatus{

    init {
        layout = R.layout.common_without_setting
        descText = ("唤醒词功能即SDK识别唤醒词，或者认为是SDK识别出用户一大段话中的\"关键词\"。"
                + " 与Android系统自身的锁屏唤醒无关\n"
                + "唤醒词是纯离线功能，需要获取正式授权文件（与离线命令词的正式授权文件是同一个）。\n"
                + " 第一次联网使用唤醒词功能自动获取正式授权文件。之后可以断网测试\n"
                + "请说“小度你好”或者 “百度一下”\n\n"
                + "集成指南：如果无法正常使用请检查正式授权文件问题:\n"
                + " 1. 是否在AndroidManifest.xml配置了APP_ID\n"
                + " 2. 是否在开放平台对应应用绑定了包名, 本demo的包名是com.baidu.speech.recognizerdemo"
                + "定义在build.gradle文件中。\n\n")
    }

    companion object {
        const val TAG = "ActivityWakeUp"
    }

    var myWakeup: MyWakeup? = null

    private var status = IStatus.STATUS_NONE

    override fun initRecog() {
        val listener = RecogWakeupListener(handler!!)
        // 改为 SimpleWakeupListener 后，不依赖handler，但将不会在UI界面上显示
        myWakeup = MyWakeup(this, listener)
    }

    private fun start() {
        val params = HashMap<String, Any>()
        params[SpeechConstant.WP_WORDS_FILE] = "assets:///WakeUp.bin"
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        // params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        // params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        // params里 "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
        myWakeup?.start(params)
    }


    open fun stop() {
        myWakeup?.stop()
    }

     override fun initView() {
        super.initView()
        btn?.setOnClickListener(View.OnClickListener {
            when (status) {
                IStatus.STATUS_NONE -> {
                    start()
                    status = STATUS_WAITING_READY
                    updateBtnTextByStatus()
                    txtLog?.text = ""
                    txtResult?.text = ""
                }
                STATUS_WAITING_READY -> {
                    stop()
                    status = IStatus.STATUS_NONE
                    updateBtnTextByStatus()
                }
                else -> {
                }
            }
        })
    }

    private fun updateBtnTextByStatus() {
        when (status) {
            IStatus.STATUS_NONE -> btn?.text = "启动唤醒"
            STATUS_WAITING_READY -> btn?.text = "停止唤醒"
            else -> {
            }
        }
    }


    override fun onDestroy() {
        myWakeup!!.release()
        super.onDestroy()
    }
}