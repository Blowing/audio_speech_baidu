package com.wujie.android.voicedemo.recognization.inputstream

import android.app.Activity
import com.wujie.android.voicedemo.util.Logger
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference

/**
 * Created by wujie on 2018/8/7/007.
 *
 */
object InFileStream {

    private var weakActivity: WeakReference<Activity>? = null

    private val TAG = "InFileStream"

    fun setContext(context: Activity) {
        InFileStream.weakActivity = WeakReference(context)
    }

    private var filename: String? = null

    private var inputS: InputStream? = null

    fun reset() {
        filename = null
        inputS = null
    }

    fun setFileName(filename: String) {
        InFileStream.filename = filename
    }

    fun setInputStream(is2: InputStream) {
        InFileStream.inputS = is2
    }

    fun create16KStream(): InputStream? {
        when {
            inputS != null -> return FileAudioInputStream(inputS!!)
            filename != null -> try {
                return FileAudioInputStream(filename!!)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            else -> return FileAudioInputStream(createFileStream())
        }
        return null
    }

    private fun createFileStream(): InputStream?{
        var res : InputStream? = null
        try {
            val inputStream = weakActivity?.get()?.assets?.open("outfile.pcm")
            Logger.info(TAG, "create input stream ok"+ inputStream?.available())
            res = FileAudioInputStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return res;
    }
}