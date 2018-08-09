package com.wujie.android.voicedemo.recognization

import android.os.Handler
import android.os.Message
import android.util.Log

/**
 * Created by wujie on 2018/8/9/009.
 */
class MessageStatusRecogListener(private val handler: Handler?): StatusRecogListener(){

    companion object {
        val TAG = "MesStatusRecogListener"
    }

    private var speechEndTime: Long = 0

    private var needTime: Boolean = true


    override fun onAsrReady() {
        super.onAsrReady()
        sendStatusMessage("引擎就绪，就开始说话.")
    }

    override fun onAsrBegin() {
        super.onAsrBegin()
        sendStatusMessage("检测到用户说话.")
    }

    override fun onAsrEnd() {
        super.onAsrEnd()
        speechEndTime = System.currentTimeMillis()
        sendMessage("检测到用户说话结束")
    }

    override fun onAsrPartialResult(results: Array<String?>?, recogResult: RecogResult) {
        super.onAsrPartialResult(results, recogResult)
        sendStatusMessage("临时识别结果，结果是"+ (results?.get(0) ?: "") + "原始json:${recogResult.origalJson}")

    }

    override fun onAsrFinalResult(results: Array<String?>?, recogResult: RecogResult) {
        super.onAsrFinalResult(results, recogResult)
        var message = "识别结束，结果是${(results?.get(0)?: "")}"
        sendStatusMessage("$message；原始json: ${recogResult.origalJson}")
        if (speechEndTime > 0) {
            val diffTime = System.currentTimeMillis() - speechEndTime
            message += "; 说话结束到识别结束耗时【$diffTime ms】"
        }
        speechEndTime = 0
        sendMessage(message, Status, true)
    }

    override fun onAsrFinish(recogResult: RecogResult) {
        super.onAsrFinish(recogResult)
        sendStatusMessage("识别一段话结束。如果是长语音的情况会继续识别下段话。")
    }

    override fun onAsrFinishError(errorCode: Int, subErrorCode: Int, errorMessage: String, descMessage: String?, recogResult: RecogResult) {
        super.onAsrFinishError(errorCode, subErrorCode, errorMessage, descMessage, recogResult)

        var message = "识别错误, 错误码：$errorCode, $subErrorCode"
        sendStatusMessage("$message:错误信息：$errorMessage; 描述信息：$descMessage")
        if (speechEndTime > 0) {
            val diffTime = System.currentTimeMillis() - speechEndTime
            message += "; 说话结束到识别结束耗时【$diffTime ms】"
        }
        speechEndTime = 0
        sendMessage(message, Status, true)
    }

    override fun onAsrLongFinish() {
        super.onAsrLongFinish()
        sendStatusMessage("长语音识别结束。")
    }

    override fun onAsrVolume(volumePercent: Int, volume: Int) {
        super.onAsrVolume(volumePercent, volume)
    }

    override fun onAsrAudio(data: ByteArray?, offset: Int, length: Int) {
        super.onAsrAudio(data, offset, length)
    }

    override fun onAsrExit() {
        super.onAsrExit()
        sendStatusMessage("识别引擎结束并空闲中")
    }

    override fun onAsrOnlineNluResult(nluResult: String) {
        super.onAsrOnlineNluResult(nluResult)
        if (!nluResult.isEmpty()) {
            sendStatusMessage("原始语义识别结果json: $nluResult")
        }
    }

    override fun onOfflineLoaded() {
        super.onOfflineLoaded()
        sendStatusMessage("【重要】asr.loaded：离线资源加载成功。没有此回调可能离线语法功能不能使用。")
    }

    override fun onOfflineUnLoaded() {
        super.onOfflineUnLoaded()
        sendStatusMessage(" 离线资源卸载成功。")
    }

    private fun sendStatusMessage(message: String) {
        sendMessage(message, Status)
    }

    private fun sendMessage( message: String) {
        sendMessage(message, IStatus.WHAT_MESSAGE_STATUS)
    }

    private fun sendMessage( message: String, what: Int) {
        sendMessage(message, what, false)
    }

    private fun sendMessage(message: String, what: Int, highlight: Boolean) {
        var message: String = message
        if (needTime && what != IStatus.WHAT_MESSAGE_STATUS) {
            message = "$message   ;time=${System.currentTimeMillis()}"
        }

        if (handler == null) {
            Log.i(TAG, message)
        }

        val msg = Message.obtain()
        msg.what = what
        msg.arg1 = Status
        if (highlight) msg.arg2 = 1
        msg.obj = "$message\n"
        handler?.sendMessage(msg)
    }
}