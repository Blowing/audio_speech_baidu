package com.wujie.android.voicedemo.recognization

import android.util.Log
import com.baidu.speech.EventListener
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.control.ErrorTranslation
import com.wujie.android.voicedemo.util.Logger
import org.json.JSONObject


class RecogEventAdapter(private val listener: IRecogListener) : EventListener{

    companion object {
        val TAG = "RecogEventAdapter"
    }

     private var currentJson : String? = null


    override fun onEvent(name: String?, params: String?, data: ByteArray?, offset: Int, length: Int) {
        currentJson = params
        val logMessage = "name:$name; params:$params"
        Log.i(TAG, logMessage)

        when(name) {
            SpeechConstant.CALLBACK_EVENT_ASR_LOADED -> listener.onOfflineLoaded()
            SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED -> listener.onOfflineUnLoaded()
            SpeechConstant.CALLBACK_EVENT_ASR_READY -> listener.onAsrReady()
            SpeechConstant.CALLBACK_EVENT_ASR_BEGIN -> listener.onAsrBegin()
            SpeechConstant.CALLBACK_EVENT_ASR_END -> listener.onAsrEnd()
            SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL -> {
                val recogResult =  RecogResult.parseJson(params)
                val results : Array<String?>? = recogResult.resultRecognition
                when {
                    recogResult.isFinalResult() -> listener.onAsrFinalResult(results, recogResult)
                    recogResult.isNluResult() -> listener.onAsrOnlineNluResult(String(data!!, offset, length))
                    recogResult.isPartialReusult() -> listener.onAsrPartialResult(results, recogResult)
                }
            }
            SpeechConstant.CALLBACK_EVENT_ASR_FINISH ->  {
                val recogResult = RecogResult.parseJson(params)
                if(recogResult.haseError()) {
                    val erroCode = recogResult.error
                    val subErrorCode = recogResult.subError
                    Logger.error(TAG, "asr error $params")
                    listener.onAsrFinishError(erroCode, subErrorCode, ErrorTranslation.recogError
                    (erroCode),recogResult.desc, recogResult)
                } else {
                    listener.onAsrFinish(recogResult)
                }
            }
            SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH -> listener.onAsrLongFinish()
            SpeechConstant.CALLBACK_EVENT_ASR_EXIT -> listener.onAsrExit()
            SpeechConstant.CALLBACK_EVENT_ASR_VOLUME ->  {
                val vol = parseVolumeJson(params)
                listener.onAsrVolume(vol.volumePercent, vol.volume)
            }
            SpeechConstant.CALLBACK_EVENT_ASR_AUDIO -> {
                if (data?.size != length) {
                    Logger.error(TAG, "internal error: asr.audio callback data length is not equal to length param")
                }
                listener.onAsrAudio(data, offset, length )
            }

        }




    }

    private fun parseVolumeJson(jsonStr: String?): Volume {
        val vol  = Volume()
        vol.origalJson = jsonStr
        try {
            val json = JSONObject(jsonStr)
            vol.volumePercent = json.optInt("volume-percent")
            vol.volume = json.optInt("volume")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vol
    }

    private inner class Volume{
         var volumePercent: Int = -1
         var volume: Int = -1
         var origalJson: String? = null
    }



}
