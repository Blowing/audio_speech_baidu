package com.wujie.android.voicedemo.ui

import android.app.Application

/**
 * Created by wujie on 2018/8/20/020.
 *
 */
class SimpleTransApplication(var digitalDialogInput: DigitalDialogInput?) : Application() {
    constructor() : this(null) {}
}