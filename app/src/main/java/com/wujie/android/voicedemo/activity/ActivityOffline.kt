package com.wujie.android.voicedemo.activity

import com.wujie.android.voicedemo.activity.setting.OfflineSetting
import com.wujie.android.voicedemo.recognization.CommonRecogParams
import com.wujie.android.voicedemo.recognization.offline.OfflineRecogParams

/**
 * Created by wujie on 2018/8/20/020.
 */
class ActivityOffline: ActivityRecog() {
    init {
        descText = ("展示离线命令词识别功能。即自定义有限的短句进行离线识别。SDK没有任意词离线识别功能。\n"
                + "测试方法： "
                + "1. 看见下方日志中asr.loaded说明离线文件加载成功。\n"
                + "2. 联网，在线识别说一句正常句子后，正式授权文件才会自动下载。\n"
                + "3. 断网可以测试。请大声说出： 打电话给妈妈\n\n"
                + "集成指南："
                + "如果集成后无法正常使用离线功能:\n"
                + " 1. 是否在开放平台对应应用绑定了包名，本demo的包名是com.baidu.speech.recognizerdemo，"
                + "定义在build.gradle文件中。\n"
                + " 2. AndroidManifest.xml是否填写了正确的APP_ID APP_KEY 及 APP_SECRET\n"
                + "\n其它提示:\n"
                + " 1. 在加载离线引擎ASR_KWS_LOAD_ENGINE输入事件中的GRAMMER参数中设置bsg文件路径。此时如同时设置SLOT_DATA参数的会覆盖bsg文件中的同名词条。\n"
                + "如果删除DEMO代码里的SLOT_DATA参数后, 您可以测试本DEMO里bsg文件自带的:“打电话给张三(离线)”\n"
                + " 2. 卸载app后正式授权文件自动删除。\n"
                + " 3. 自定义离线命令词的bsg文件，在http://yuyin.baidu.com/asr 下载。\n ")
        enableOffline = true
        settingActivityClass = OfflineSetting::class.java
    }

    override fun getApiParams(): CommonRecogParams {
        return OfflineRecogParams(this)
    }
}