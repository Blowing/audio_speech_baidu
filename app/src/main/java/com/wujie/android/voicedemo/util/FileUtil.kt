package com.wujie.android.voicedemo.util

import android.content.res.AssetManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Created by wujie on 2018/8/9/009.
 */
object FileUtil {

    fun makeDir(dirPath: String) : Boolean {
        val file = File(dirPath)
        return when {
            file.exists() -> true
            else -> file.mkdirs()
        }
    }

    fun getContentFromAssetsFile(assets: AssetManager, source: String):String {
        var isput: InputStream? = null
        var fos: FileOutputStream? = null
        var result = ""
        try {
            isput = assets.open(source)
            var buffer = ByteArray(isput.available())
            isput.read(buffer)
            result = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    @Throws (IOException::class)
    fun copyFromAssets(assets: AssetManager, source: String, dest: String, isCover: Boolean) {
        val file = File(dest)
        var isCopyed = false

        if(isCover ||(!isCover && !file.exists())) {
            var input : InputStream? = null
            var fos: FileOutputStream? = null

            try {
                input = assets.open(source)
                val path = dest
                fos = FileOutputStream(path)

                var buffer = ByteArray(1024)
                var size = 0
                while (input.read(buffer, 0, 1024) >= 0) {
                    fos.write(buffer, 0, buffer.size)
                }
                isCopyed = true

            }finally {
                if(fos != null) {
                    try {
                        fos.close()
                    } finally {
                        if (input != null) {
                            input.close()
                        }
                    }
                }
            }




        }
    }

}