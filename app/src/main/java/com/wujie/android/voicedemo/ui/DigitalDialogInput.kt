package com.wujie.android.voicedemo.ui

import com.wujie.android.voicedemo.control.MyRecognizer
import com.wujie.android.voicedemo.recognization.ChainRecogListener

/**
 * Created by wujie on 2018/8/20/020.
 */
class DigitalDialogInput(val myRecognizer: MyRecognizer, val listener: ChainRecogListener, val
startParams: Map<String, Any>) {
    private val code: Int = 0
}