package com.wujie.android.voicedemo.activity

/**
 * Created by wujie on 2018/8/20/020.
 */
class ActivityUiDialog : ActivityOnline() {
    init {

        descText = "多了UI 对话框。使用在线普通识别功能(含长语音)\n" +
                "请先测试“在线识别”界面\n"+
                "识别逻辑在BaiduASRDialog类\n"+
                "\n"+"集成指南：\n"+
                "相关资源文件：src/resources/*.properites。 src/res 各个目录下以bdsppech_开头的资源文件名\n"+
                "1. 测试“在线识别“，查看参数和回调有问题么\n"+
                "2. 同样的输入参数，使用“UI”，查看回调有问题么\n"+
                "3. 查看界面及功能是否正常\n\n"

    }
}