package com.wujie.android.voicedemo.activity

import com.wujie.android.voicedemo.activity.setting.NluSetting
import com.wujie.android.voicedemo.recognization.CommonRecogParams
import com.wujie.android.voicedemo.recognization.nlu.NluRecogParams

/**
 * Created by wujie on 2018/8/16/016.
 */
class ActivityNlu : ActivityRecog(){

    init {
        descText = ("语义解析功能是指录音被识别出文字后, 对文字进行分析，如进行分词并尽可能获取文字的意图。\n"
                + "语义解析分为在线语义和本地语义：\n"
                + "1. 在线语义由百度服务器完成。 请点“设置”按钮选择开启“在线语义”。在线语义必须选择搜索模型。\n"
                + "2. 本地语义解析，请点“设置”按钮选择“在线+离线命令词+ SLOT_DATA”，“开启本地语义解析”。大声说“打电话给赵琦”\n"
                + "3. 离线命令词语义， 请测试”离线命令词”后，断网， 请点“设置”按钮选择“离在线+离线命令词+ SLOT_DATA”，“开启本地语义解析”。大声说“打电话给赵琦”\n\n"
                + "集成指南：\n"
                + "本地语义：在开始识别ASR_START输入事件中的GRAMMER参数中设置bsg文件路径。如同时设置SLOT_DATA参数的会覆盖bsg文件中的同名词条。\n"
                + "如果开启离线命令词功能的话，本地语义文件参数可以不用输入。\n\n")

        enableOffline = true // 请确认不使用离线命令词功能后，改为false
    }

    init {
        settingActivityClass = NluSetting::class.java
    }

    override fun getApiParams(): CommonRecogParams {
        return NluRecogParams(this)
    }
}