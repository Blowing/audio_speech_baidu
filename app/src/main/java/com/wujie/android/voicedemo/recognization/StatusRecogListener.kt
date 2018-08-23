package com.wujie.android.voicedemo.recognization

import android.util.Log

/**
 * Created by wujie on 2018/8/9/009.
 *
 */
open class StatusRecogListener : IRecogListener, IStatus {
    companion object {
        val TAG = "StatusRecogListener"
    }

    protected var Status = IStatus.STATUS_NONE

    override fun onAsrReady() {
        Status = IStatus.STATUS_READY
    }

    override fun onAsrBegin() {
        Status = IStatus.STATUS_SPEAKING
    }

    override fun onAsrEnd() {
        Status = IStatus.STATUS_RECOGNITION
    }

    override fun onAsrPartialResult(results: Array<String?>?, recogResult: RecogResult) {
    }

    override fun onAsrFinalResult(results: Array<String?>?, recogResult: RecogResult) {
        Status = IStatus.STATUS_FINISHED
    }

    override fun onAsrFinish(recogResult: RecogResult) {
        Status = IStatus.STATUS_FINISHED
    }

    override fun onAsrFinishError(errorCode: Int, subErrorCode: Int, errorMessage: String, descMessage: String?, recogResult: RecogResult) {
        Status = IStatus.STATUS_FINISHED
    }


    override fun onAsrLongFinish() {
        Status = IStatus.STATUS_FINISHED
    }

    override fun onAsrVolume(volumePercent: Int, volume: Int) {
        Log.i(TAG, "音量百分比$volumePercent; 音量$volume")
    }

    override fun onAsrAudio(data: ByteArray?, offset: Int, length: Int) {
    }

    override fun onAsrExit() {
        Status = IStatus.STATUS_NONE
    }

    override fun onAsrOnlineNluResult(nluResult: String) {
        Status = IStatus.STATUS_FINISHED
    }

    override fun onOfflineLoaded() {
    }

    override fun onOfflineUnLoaded() {
    }
}