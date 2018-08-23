package com.wujie.android.voicedemo.activity.test

import android.os.Handler
import com.wujie.android.voicedemo.recognization.IRecogListener
import com.wujie.android.voicedemo.recognization.RecogResult
import com.wujie.android.voicedemo.util.Logger

/**
 * Created by wujie on 2018/8/23/023.
 */
class AlarmListener(private val handler: Handler) : IRecogListener {


    /**
     * ASR_START 输入事件调用后，引擎准备完毕
     */
    override fun onAsrReady() {

    }

    /**
     * onAsrReady后检查到用户开始说话
     */
    override fun onAsrBegin() {

    }

    /**
     * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
     */
    override fun onAsrEnd() {

    }

    /**
     * onAsrBegin 后 随着用户的说话，返回的临时结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    override fun onAsrPartialResult(results: Array<String?>?, recogResult: RecogResult) {

    }

    /**
     * 最终的识别结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    override fun onAsrFinalResult(results: Array<String?>?, recogResult: RecogResult) {
        sendMessage(results!![0]!!, 901)
    }

    override fun onAsrFinish(recogResult: RecogResult) {

    }

    override fun onAsrFinishError(errorCode: Int, subErrorCode: Int, errorMessage: String,
                                  descMessage: String?,
                         recogResult: RecogResult) {
        sendMessage(recogResult.origalJson!!, -801)
    }

    /**
     * 长语音识别结束
     */
    override fun onAsrLongFinish() {

    }

    override fun onAsrVolume(volumePercent: Int, volume: Int) {

    }

    override fun onAsrAudio(data: ByteArray?, offset: Int, length: Int) {

    }

    override fun onAsrExit() {
        sendMessage("", 900)
    }

    override fun onAsrOnlineNluResult(nluResult: String) {

    }

    override fun onOfflineLoaded() {

    }

    override fun onOfflineUnLoaded() {

    }

    private fun sendMessage(message: String, what: Int) {
        val msg = handler.obtainMessage()
        msg.what = what
        msg.obj = message
        handler.sendMessage(msg)
    }

    private fun showMessage(msg: String, isError: Boolean) {
        if (isError) {
            Logger.error(msg)
        } else {
            Logger.info(msg)
        }
    }
}
