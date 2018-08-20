package com.wujie.android.voicedemo.activity

import android.os.Message
import android.preference.PreferenceManager
import com.wujie.android.voicedemo.control.MyRecognizer
import com.wujie.android.voicedemo.recognization.CommonRecogParams
import com.wujie.android.voicedemo.recognization.IStatus
import com.wujie.android.voicedemo.recognization.IStatus.Companion.STATUS_FINISHED
import com.wujie.android.voicedemo.recognization.IStatus.Companion.STATUS_NONE
import com.wujie.android.voicedemo.recognization.IStatus.Companion.STATUS_READY
import com.wujie.android.voicedemo.recognization.IStatus.Companion.STATUS_RECOGNITION
import com.wujie.android.voicedemo.recognization.IStatus.Companion.STATUS_SPEAKING
import com.wujie.android.voicedemo.recognization.MessageStatusRecogListener
import com.wujie.android.voicedemo.recognization.offline.OfflineRecogParams

/**
 * Created by wujie on 2018/8/7/007.
 *
 */
abstract class ActivityRecog : ActivityCommon(), IStatus {

    protected var myRecognizer: MyRecognizer? = null

    protected var apiParam: CommonRecogParams? = null

    protected var enableOffline = false

    protected var status = 0

    companion object {
        const val TAG = "ActivityRecog"
    }

    protected abstract fun getApiParams(): CommonRecogParams

    override fun initRecog() {
        val listener = MessageStatusRecogListener(handler)
        myRecognizer = MyRecognizer(this, listener)
        apiParam = getApiParams()
        status = IStatus.STATUS_NONE
        if (enableOffline)
            myRecognizer?.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams())

    }

    private fun start() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val params = apiParam?.fetch(sp)
        myRecognizer?.start(params!!)
    }

    private fun stop() {
        myRecognizer?.stop()
    }

    private fun cancel() {
        myRecognizer?.cancel()
    }


    override fun onDestroy() {
        myRecognizer?.release()
        super.onDestroy()
    }

    override fun initView() {
        super.initView()
        btn!!.setOnClickListener {
            when (status) {
                IStatus.STATUS_NONE -> {
                    start()
                    status = IStatus.STATUS_WAITING_READY
                    updateBtnTextByStatus()
                    txtLog?.text = ""
                    txtResult?.text = ""
                }

                IStatus.STATUS_WAITING_READY, IStatus.STATUS_READY,
                IStatus.STATUS_SPEAKING, IStatus.STATUS_FINISHED,
                IStatus.STATUS_RECOGNITION -> {
                    stop()
                    status = IStatus.STATUS_STOPPED
                    updateBtnTextByStatus()
                }
                IStatus.STATUS_STOPPED -> {
                    cancel()
                    status = IStatus.STATUS_NONE
                    updateBtnTextByStatus()
                }

            }
        }
    }

    private fun updateBtnTextByStatus() {
        when (status) {
            IStatus.STATUS_NONE -> {
                btn?.text = "开始录音"
                btn?.isEnabled = true
                setting?.isEnabled = true
            }

            IStatus.STATUS_WAITING_READY,
            IStatus.STATUS_READY,
            IStatus.STATUS_SPEAKING,
            IStatus.STATUS_RECOGNITION -> {
                btn?.text = "停止录音"
                btn?.isEnabled = true
                setting?.isEnabled = false
            }
            IStatus.STATUS_STOPPED -> {
                btn?.text = "取消整个识别过程"
                btn?.isEnabled = true
                setting?.isEnabled = false
            }

        }
    }


    override fun handleMsg(msg: Message?) {
        super.handleMsg(msg)

        when (msg?.what) {
        // 处理MessageStatusRecogListener中的状态回调
            STATUS_FINISHED -> {
                if (msg.arg2 == 1) {
                    txtResult?.text = msg.obj.toString()
                }
                status = msg.what
                updateBtnTextByStatus()
            }

            STATUS_NONE,
            STATUS_READY,
            STATUS_SPEAKING,
            STATUS_RECOGNITION -> {
                status = msg.what
                updateBtnTextByStatus()
            }
        }


    }
}