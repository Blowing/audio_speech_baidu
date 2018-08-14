package com.wujie.android.voicedemo.recognization.inputstream

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException
import java.io.InputStream

/**
 * Created by wujie on 2018/8/14/014.
 */
class MyMicrophoneInputStream : InputStream() {

    var isStarted = false
    companion object {
        var audioRecord: AudioRecord? = null
        private var input: MyMicrophoneInputStream ? = null
        val TAG = "MyMicrophoneInputStream"

        val instance: MyMicrophoneInputStream?
        get() {
            if(input == null) {
                synchronized(MyMicrophoneInputStream::class.java) {
                    if (input == null) {
                        input = MyMicrophoneInputStream()
                    }
                }
            }
            return input
        }

    }

    init {
        if(audioRecord == null) {
            val bufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat
                    .CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT) * 16
            audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                    16000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        }
    }

    @Throws(IOException::class)
    override fun read(): Int {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        if (!isStarted) {
            start()
            isStarted = true
        }

        try {
            return audioRecord!!.read(b, off, len)
        } catch (e: Exception) {
            Log.e(TAG, e.javaClass.simpleName, e)
            throw e
        }
    }

    fun start() {
        Log.i(TAG, "MyMicrophoneInputStream start recoding")

        try {
            if (audioRecord == null || audioRecord!!.state != AudioRecord.STATE_INITIALIZED) {
                throw IllegalStateException("startRecording() called on an uninitialized AudioRecord.${audioRecord == null}")
            }
            audioRecord!!.startRecording()
        } catch (e: Exception) {
            Log.i(TAG, e.javaClass.simpleName, e)
        }
        Log.i(TAG, " MyMicrophoneInputStream start recoding finished")
    }

    @Throws(IOException::class)
    override fun close() {
        Log.i(TAG, " MyMicrophoneInputStream close")
        if (audioRecord != null) {
            audioRecord!!.stop()
            // audioRecord.release(); 程序结束别忘记自行释放
            isStarted = false
        }

    }
}