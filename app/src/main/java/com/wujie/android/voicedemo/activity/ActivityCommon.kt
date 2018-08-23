package com.wujie.android.voicedemo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.StrictMode
import android.os.StrictMode.setThreadPolicy
import android.os.StrictMode.setVmPolicy
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.wujie.android.voicedemo.R
import com.wujie.android.voicedemo.recognization.inputstream.InFileStream
import com.wujie.android.voicedemo.util.Logger

abstract class ActivityCommon : AppCompatActivity() {
    protected var txtLog: TextView? = null
    protected var btn: Button? = null
    protected var setting: Button? = null
    protected var txtResult: TextView? = null

    protected var handler: Handler? = null

    protected var descText: String? = null

    protected var layout = R.layout.common

    protected var settingActivityClass: Class<*>? = null

    protected var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStrictMode()
        setContentView(layout)
        InFileStream.setContext(this)
        initView()
        handler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                handleMsg(msg)
            }
        }
        Logger.setHandler(handler = handler as Handler)
        initPermission()
        initRecog()
    }

    abstract fun initRecog()

    open fun handleMsg(msg: Message?) {
        txtLog?.append(msg?.obj.toString() + "\n")
    }


    @SuppressLint("SetTextI18n")
     open fun initView() {
        txtResult = findViewById(R.id.txtResult)
        txtLog = findViewById(R.id.txtLog)

        btn = findViewById(R.id.btn)
        txtLog?.text = "${this.descText}\n"
        setting = findViewById(R.id.setting)

        if (settingActivityClass != null) {
            setting?.setOnClickListener {
                running = true
                intent = Intent(this, settingActivityClass)
                startActivityForResult(intent, 1)
            }
        }
    }

    private fun initPermission() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val toApplyList = ArrayList<String>()
        permissions.forEach {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, it)) {
                toApplyList.add(it)
                // 进入到这里代表没有权限.
            }
        }
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(arrayOf()),
                    123)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 此处为android 6.0以上动态授权的回调，用户自行实现
    }

    private fun setStrictMode() {
        setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())
    }

}
