package com.wujie.android.voicedemo.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.AndroidRuntimeException
import com.baidu.speech.asr.SpeechConstant
import com.wujie.android.voicedemo.control.MyRecognizer
import com.wujie.android.voicedemo.recognization.IRecogListener
import com.wujie.android.voicedemo.recognization.RecogResult
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by wujie on 2018/8/20/020.
 */
abstract class BaiduASRDialog : Activity() {

    companion object {
        /**
         * 提示语
         */
        const val PARAM_PORMPT_TEXT = "prompt_text"
        val STATUS_None = 0
        val STATUS_WaitingReady = 2
        val STATUS_Ready = 3
        val STATUS_Speaking = 4
        val STATUS_Recognition = 5

        const val TAG = "BaiduASRDialog"
    }

    @Volatile
    private var mIsRunning = false

     var mPrompt: String = ""

    protected var status = STATUS_None

    var mParams = Bundle()

    private var input: DigitalDialogInput? = null

    private var myRecognizer: MyRecognizer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        input = (application as SimpleTransApplication).digitalDialogInput
        val listener = input!!.listener
        listener.addListener(DialogListener())
        myRecognizer = input!!.myRecognizer
        mParams.putAll(intent.extras)
    }

    fun startRecognition() {
        mPrompt = mParams.getString(PARAM_PORMPT_TEXT)
        mIsRunning = true
        onRecongnitionStart()
        val params = input!!.startParams as HashMap
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = true
        myRecognizer?.start(input!!.startParams)
    }

    fun speakFinish() {
        myRecognizer?.stop()
    }

    fun cancleRecognition() {
        myRecognizer?.cancel()
        status = STATUS_None
    }

    private fun checkConfig() {
        try {
            val info = packageManager.getActivityInfo(ComponentName(packageName, javaClass.name),
                    PackageManager.GET_META_DATA)
            if (info.exported) {
                throw AndroidRuntimeException(javaClass.name + "android:exported' should be false, please modify AndroidManifest.xml")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        myRecognizer?.cancel()
        if (!isFinishing) finish()
    }

    abstract fun onRecongnitionStart()

    abstract fun onPrepared()

    abstract fun onBeginningOfSpeech()

    abstract fun onVolumeChanged(volume: Int)

    abstract fun onEndOfSpeech()

    abstract fun onFinish(errorType: Int, errorCode: Int)

    abstract fun onPartialResults(results: Array<String?>?)


    inner class DialogListener : IRecogListener {
        override fun onAsrReady() {
            status = STATUS_None
            onPrepared()
        }

        override fun onAsrBegin() {
            status = STATUS_Speaking
            onBeginningOfSpeech()
        }

        override fun onAsrEnd() {
            status = STATUS_Recognition
            onEndOfSpeech()
        }

        override fun onAsrPartialResult(results: Array<String?>?, recogResult: RecogResult) {
            onPartialResults(results)
        }

        override fun onAsrFinalResult(results: Array<String?>?, recogResult: RecogResult) {
            status = STATUS_None
            mIsRunning = false
            onFinish(0,0)

            val intentResult = Intent()
            val list = ArrayList<String>()
            list.addAll(Arrays.asList(results) as ArrayList<String>)
            intentResult.putStringArrayListExtra("results", list)
            setResult(RESULT_OK, intentResult)
        }

        override fun onAsrFinish(recogResult: RecogResult) {
            mIsRunning = false
            finish()
        }

        override fun onAsrFinishError(errorCode: Int, subErrorCode: Int, errorMessage: String, descMessage: String?, recogResult: RecogResult) {
            onFinish(errorCode, subErrorCode)
        }

        override fun onAsrLongFinish() {
            mIsRunning = false
            finish()
        }

        override fun onAsrVolume(volumePercent: Int, volume: Int) {
            onVolumeChanged(volumePercent)
        }

        override fun onAsrAudio(data: ByteArray?, offset: Int, length: Int) {
        }

        override fun onAsrExit() {
        }

        override fun onAsrOnlineNluResult(nluResult: String) {
        }

        override fun onOfflineLoaded() {
        }

        override fun onOfflineUnLoaded() {
        }

    }
}