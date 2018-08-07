package com.wujie.android.voicedemo.recognization.inputstream

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class FileAudioInputStream() : InputStream() {



    private var nextSleepTime: Long = -1

    private var totalSleepMs: Long = 0

    private val TAG = "FileAudioInputStream"

    @Throws(FileNotFoundException::class)
    constructor(inputS: String) : this() {
        inputStream = FileInputStream(inputS)
    }
    constructor(inputS: InputStream?) : this() {
        inputStream = inputS
    }



    private var inputStream: InputStream? = null

    constructor(parcel: Parcel) : this() {
        nextSleepTime = parcel.readLong()
        totalSleepMs = parcel.readLong()
    }


    @Throws(IOException::class)
    override fun read() : Int {
        throw UnsupportedOperationException()
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        val bytePerMs = 16000*2 / 1000
        var count = bytePerMs * 20 // 10ms 音频数据
        if (len < count) count = len

        if (nextSleepTime > 0) {
            try {
                val sleepMs = nextSleepTime - System.currentTimeMillis()
                if (sleepMs > 0) {
                    Thread.sleep(sleepMs)
                    totalSleepMs += sleepMs
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        val r = inputStream?.read(b, off, count)
        nextSleepTime = System.currentTimeMillis() + r!!.div(bytePerMs)
        return r
    }

    override fun close() {
        super.close()
        Log.i(TAG, "time sleeped $totalSleepMs")
        inputStream?.close()
    }


    companion object CREATOR : Parcelable.Creator<FileAudioInputStream> {
        override fun createFromParcel(parcel: Parcel): FileAudioInputStream {
            return FileAudioInputStream(parcel)
        }

        override fun newArray(size: Int): Array<FileAudioInputStream?> {
            return arrayOfNulls(size)
        }
    }
}
