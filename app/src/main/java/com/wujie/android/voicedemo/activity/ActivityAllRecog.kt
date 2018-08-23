package com.wujie.android.voicedemo.activity

import com.wujie.android.voicedemo.activity.setting.AllSetting
import com.wujie.android.voicedemo.recognization.CommonRecogParams
import com.wujie.android.voicedemo.recognization.all.AllRecogParams

/**
 * Created by wujie on 2018/8/23/023.
 */
class ActivityAllRecog : ActivityRecog() {
    override fun getApiParams(): CommonRecogParams {
        return AllRecogParams(this)
    }

    init {
        settingActivityClass = AllSetting::class.java
    }
}