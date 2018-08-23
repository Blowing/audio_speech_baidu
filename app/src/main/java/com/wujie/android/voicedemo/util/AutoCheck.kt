package com.wujie.android.voicedemo.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.util.Log
import com.baidu.speech.asr.SpeechConstant
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Created by wujie on 2018/8/23/023.
 */
class AutoCheck(private val context: Context, private val handler: Handler, private val enableOffline: Boolean) {
    private var checks: LinkedHashMap<String, Check> = LinkedHashMap()

    private var hasError: Boolean = false
    private var isFinished = false

    private var name: String? = null

    init {
        checks = LinkedHashMap()
    }

    fun checkAsr(params: Map<String, Any>) {
        val t = Thread(Runnable {
            val obj = checkAsrInternal(params)
            name = "识别"
            synchronized(obj) {
                // 偶发，同步线程信息
                isFinished = true
                val msg = handler.obtainMessage(100, obj)
                handler.sendMessage(msg)
            }
        })
        t.start()
    }

    fun obtainErrorMessage(): String {
        val config = PrintConfig()
        return formatString(config)
    }

    fun obtainDebugMessage(): String {
        val config = PrintConfig()
        config.withInfo = true
        return formatString(config)
    }

    fun obtainAllMessage(): String {
        val config = PrintConfig()
        config.withLog = true
        config.withInfo = true
        config.withLogOnSuccess = true
        return formatString(config)
    }

    private fun formatString(config: PrintConfig): String {
        val sb = StringBuilder()
        hasError = false

        for ((testName, check) in checks) {
            if (check.hasError()) {
                if (!hasError) {
                    hasError = true
                }

                sb.append("【错误】【").append(testName).append(" 】  ").append(check.errorMessage).append("\n")
                if (check.hasFix()) {
                    sb.append("【修复方法】【").append(testName).append(" 】  ").append(check.fixMessage).append("\n")
                }
            } else if (config.withEachCheckInfo) {
                sb.append("【无报错】【").append(testName).append(" 】  ").append("\n")
            }
            if (config.withInfo && check.hasInfo()) {
                sb.append("【请手动检查】【").append(testName).append("】 ").append(check.infoMessage).append("\n")
            }
            if (config.withLog && (config.withLogOnSuccess || hasError) && check.hasLog()) {
                sb.append("【log】:" + check.getLogMessage()).append("\n")
            }
        }
        if (!hasError) {
            sb.append("【$name】集成自动排查工具： 恭喜没有检测到任何问题\n")
        }
        return sb.toString()
    }

    private fun checkAsrInternal(params: Map<String, Any>): AutoCheck {
        commonSetting(params)
        checks["外部音频文件存在校验"] = FileCheck(context, params, SpeechConstant.IN_FILE)
        checks["离线命令词及本地语义bsg文件存在校验"] = FileCheck(context, params, SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH)
        for ((_, check) in checks) {
            check.check()
            if (check.hasError()) {
                break
            }
        }
        return this
    }

    private fun commonSetting(params: Map<String, Any>) {
        checks["检查申请的Android权限"] = PermissionCheck(context)
        checks["检查so文件是否存在"] = JniCheck(context)
        var infoCheck: AppInfoCheck? = null
        try {
            infoCheck = AppInfoCheck(context, params)
            checks["检查AppId AppKey SecretKey"] = infoCheck
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "检查AppId AppKey SecretKey 错误", e)
            return
        }

        if (enableOffline) {
            checks["检查包名"] = ApplicationIdCheck(context, infoCheck!!.appId!!)
        }

    }

    private class PrintConfig {
        var withEachCheckInfo = false
        var withInfo = false
        var withLog = false
        var withLogOnSuccess = false
    }


    private class PermissionCheck(private val context: Context) : Check() {

        override fun check() {
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET,
                    // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE)

            val toApplyList = ArrayList<String>()

            for (perm in permissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, perm)) {
                    toApplyList.add(perm)
                    // 进入到这里代表没有权限.
                }
            }
            if (!toApplyList.isEmpty()) {
                errorMessage = "缺少权限：$toApplyList"
                fixMessage = "请从AndroidManifest.xml复制相关权限"
            }
        }
    }

    private class JniCheck(private val context: Context) : Check() {

        private var soNames: Array<String>? = null

        init {
            if (isOnlineLited) {
                soNames = arrayOf("libBaiduSpeechSDK.so", "libvad.dnn.so")
            } else {
                soNames = arrayOf("libBaiduSpeechSDK.so", "libvad.dnn.so", "libbd_easr_s1_merge_normal_20151216.dat.so", "libbdEASRAndroid.so", "libbdSpilWakeup.so")
            }
        }

        override fun check() {
            val path = context.applicationInfo.nativeLibraryDir
            appendLogMessage("Jni so文件目录 $path")
            val files = File(path).listFiles()
            val set = TreeSet<String>()
            if (files != null) {
                for (file in files) {
                    set.add(file.name)
                }
            }
            // String debugMessage = "Jni目录内文件: " + set.toString();
            // boolean isSuccess = true;
            for (name in soNames!!) {
                if (!set.contains(name)) {
                    errorMessage = "Jni目录" + path + " 缺少so文件：" + name + "， 该目录文件列表: " + set.toString()
                    fixMessage = "如果您的app内没有其它so文件，请复制demo里的src/main/jniLibs至同名目录。" + " 如果app内有so文件，请合并目录放一起(注意目录取交集，多余的目录删除)。"
                    break
                }
            }
        }
    }

    private class AppInfoCheck @Throws(PackageManager.NameNotFoundException::class)
    constructor(context: Context, params: Map<String, Any>) : Check() {
         var appId: String? = null
        private var appKey: String? = null
        private var secretKey: String? = null

        init {
            val metaData = context.packageManager.getApplicationInfo(context.packageName,
                    PackageManager.GET_META_DATA).metaData
            if (params[SpeechConstant.APP_ID] != null) {
                appId = params[SpeechConstant.APP_ID].toString()
            } else {
                appId = "" + metaData.getInt("com.baidu.speech.APP_ID")
            }
            if (params[SpeechConstant.APP_KEY] != null) {
                appKey = params[SpeechConstant.APP_KEY].toString()
            } else {
                appKey = metaData.getString("com.baidu.speech.API_KEY")
            }

            if (params[SpeechConstant.SECRET] != null) {
                secretKey = params[SpeechConstant.SECRET].toString()
            } else {
                secretKey = metaData.getString("com.baidu.speech.SECRET_KEY")
            }
        }


        override fun check() {
            do {
                appendLogMessage("try to check appId $appId ,appKey=$appKey ,secretKey$secretKey")
                if (appId == null || appId!!.isEmpty()) {
                    errorMessage = "appId 为空"
                    fixMessage = "填写appID"
                    break
                }
                if (appKey == null || appKey!!.isEmpty()) {
                    errorMessage = "appKey 为空"
                    fixMessage = "填写appID"
                    break
                }
                if (secretKey == null || secretKey!!.isEmpty()) {
                    errorMessage = "secretKey 为空"
                    fixMessage = "secretKey"
                    break
                }


                try {
                    checkOnline()
                } catch (e: UnknownHostException) {
                    infoMessage = "无网络或者网络不连通，忽略检测 : " + e.message
                } catch (e: Exception) {
                    errorMessage = e.javaClass.canonicalName + ":" + e.message
                    fixMessage = " 重新检测appId， appKey， appSecret是否正确"
                }

            } while (false)
        }

        @Throws(Exception::class)
        fun checkOnline() {
            val urlpath = ("http://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials&client_id="
                    + appKey + "&client_secret=" + secretKey)
            val url = URL(urlpath)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 1000
            val `is` = conn.inputStream
            val reader = BufferedReader(InputStreamReader(`is`))
            val result = StringBuilder()
            var line: String? = ""
            do {
                line = reader.readLine()
                if (line != null) {
                    result.append(line)
                }
            } while (line != null)
            val res = result.toString()
            if (!res.contains("audio_voice_assistant_get")) {
                errorMessage = "appid:$appId,没有audio_voice_assistant_get 权限，请在网页上开通\"语音识别\"能力"
                fixMessage = "secretKey"
                return
            }
            appendLogMessage("openapi return $res")
            val jsonObject = JSONObject(res)
            val error = jsonObject.optString("error")
            if (error != null && !error.isEmpty()) {
                errorMessage = "appkey secretKey 错误, error:$error, json is$result"
                fixMessage = " 重新检测appId对应的 appKey， appSecret是否正确"
                return
            }
            val token = jsonObject.getString("access_token")
            if (token == null || !token.endsWith("-" + appId!!)) {
                errorMessage = "appId 与 appkey及 appSecret 不一致。appId = $appId ,token = $token"
                fixMessage = " 重新检测appId对应的 appKey， appSecret是否正确"
            }
        }
    }

    private class ApplicationIdCheck(private val context: Context, private val appId: String) : Check() {

        private val applicationId: String
            get() = context.packageName

        override fun check() {
            infoMessage = ("如果您集成过程中遇见离线命令词或者唤醒初始化问题，请检查网页上appId：" + appId
                    + "  应用填写了Android包名："
                    + applicationId)
        }
    }

    private class FileCheck(private val context: Context, private val params: Map<String, Any>, private val key: String) : Check() {
        private var allowRes = false
        private var allowAssets = true

        init {
            if (key == SpeechConstant.IN_FILE) {
                allowRes = true
                allowAssets = false
            }
        }

        override fun check() {
            if (!params.containsKey(key)) {
                return
            }
            val value = params[key].toString()
            if (allowAssets) {
                val len = "asset".length
                val totalLen = len + ":///".length
                if (value.startsWith("asset")) {
                    val filename = value.substring(totalLen)
                    if (":///" != value.substring(len, totalLen) || filename.isEmpty()) {
                        errorMessage = "参数：" + key + "格式错误：" + value
                        fixMessage = "修改成" + "assets:///sdcard/xxxx.yyy"
                    }
                    try {
                        context.assets.open(filename)
                    } catch (e: IOException) {
                        errorMessage = "assets 目录下，文件不存在：$filename"
                        fixMessage = "demo的assets目录是：src/main/assets"
                        e.printStackTrace()
                    }

                    appendLogMessage("asset 检验完毕：$filename")
                }
            }
            if (allowRes) {
                val len = "res".length
                val totalLen = len + ":///".length
                if (value.startsWith("res")) {
                    val filename = value.substring(totalLen)
                    if (":///" != value.substring(len, totalLen) || filename.isEmpty()) {
                        errorMessage = "参数：" + key + "格式错误：" + value
                        fixMessage = "修改成" + "res:///com/baidu/android/voicedemo/16k_test.pcm"
                    }
                    val `is` = javaClass.classLoader.getResourceAsStream(filename)
                    if (`is` == null) {
                        errorMessage = "res，文件不存在：$filename"
                        fixMessage = "demo的res目录是：app/src/main/resources"
                    }
                    appendLogMessage("res 检验完毕：$filename")
                }
            }
            if (value.startsWith("/")) {
                if (!File(value).canRead()) {
                    errorMessage = "文件不存在：$value"
                    fixMessage = "请查看文件是否存在"
                }
                appendLogMessage("文件路径 检验完毕：$value")
            }
        }
    }

    private abstract class Check {
        var errorMessage: String? = null
            protected set

        var fixMessage: String? = null
            protected set

        var infoMessage: String? = null
            protected set

        protected var logMessage: StringBuilder

        init {
            logMessage = StringBuilder()
        }

        abstract fun check()

        fun hasError(): Boolean {
            return errorMessage != null
        }

        fun hasFix(): Boolean {
            return fixMessage != null
        }

        fun hasInfo(): Boolean {
            return infoMessage != null
        }

        fun hasLog(): Boolean {
            return !logMessage.toString().isEmpty()
        }

        fun appendLogMessage(message: String) {
            logMessage.append(message + "\n")
        }

        fun getLogMessage(): String {
            return logMessage.toString()
        }


    }

    companion object {
        val isOnlineLited = false //是否只需要是纯在线识别功能

        private val TAG = "AutoCheck"
    }


}