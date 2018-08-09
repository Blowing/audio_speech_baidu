package com.wujie.android.voicedemo.recognization

/**
 * Created by wujie on 2018/8/9/009.
 */
class ChainRecogListener: IRecogListener {

    private var listeners: ArrayList<IRecogListener> = arrayListOf()

    fun addListener(listener: IRecogListener) {
        listeners.add(listener)
    }

    override fun onAsrReady() {
        listeners.forEach{
            it.onAsrReady()
        }
    }

    override fun onAsrBegin() {
        listeners.forEach{
            it.onAsrBegin()
        }
    }

    override fun onAsrEnd() {
        listeners.forEach {
            it.onAsrEnd()
        }
    }

    override fun onAsrPartialResult(results: Array<String?>?, recogResult: RecogResult) {
        listeners.forEach { onAsrPartialResult(results, recogResult) }
    }

    override fun onAsrFinalResult(results: Array<String?>?, recogResult: RecogResult) {
        listeners.forEach { onAsrFinalResult(results, recogResult) }
    }

    override fun onAsrFinish(recogResult: RecogResult) {
        listeners.forEach { onAsrFinish(recogResult) }
    }

    override fun onAsrFinishError(errorCode: Int, subErrorCode: Int, errorMessage: String, descMessage: String?, recogResult: RecogResult) {
        listeners.forEach { onAsrFinishError(errorCode, subErrorCode, errorMessage, descMessage,
                recogResult) }
    }

    override fun onAsrLongFinish() {
        listeners.forEach { onAsrLongFinish() }
    }

    override fun onAsrVolume(volumePercent: Int, volume: Int) {
        listeners.forEach { onAsrVolume(volumePercent, volume) }
    }

    override fun onAsrAudio(data: ByteArray?, offset: Int, length: Int) {
        listeners.forEach { onAsrAudio(data, offset, length) }
    }

    override fun onAsrExit() {
        listeners.forEach { onAsrExit() }
    }

    override fun onAsrOnlineNluResult(nluResult: String) {
        listeners.forEach { onAsrOnlineNluResult(nluResult) }
    }

    override fun onOfflineLoaded() {
        listeners.forEach {
            onOfflineLoaded()
        }
    }

    override fun onOfflineUnLoaded() {
        listeners.forEach {
            onOfflineUnLoaded()
        }
    }
}