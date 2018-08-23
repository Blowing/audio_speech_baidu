package com.wujie.android.voicedemo.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.HONEYCOMB
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.speech.SpeechRecognizer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.wujie.android.voicedemo.R
import java.util.*

/**
 * Created by wujie on 2018/8/22/022.
 */
class BaiduASRDigitalDialog: BaiduASRDialog() {
    override fun onRecongnitionStart() {
    }


    private val TAG = "BSDigitalDialog"

    // 国际化标识定义Begin
    private val KEY_TIPS_ERROR_SILENT = "tips.error.silent"

    private val KEY_TIPS_ERROR_DECODER = "tips.error.decoder"

    private val KEY_TIPS_ERROR_SPEECH_TOO_SHORT = "tips.error.speech_too_short"

    private val KEY_TIPS_ERROR_SPEECH_TOO_LONG = "tips.error.speech_too_long"

    private val KEY_TIPS_ERROR_NETWORK = "tips.error.network"

    private val KEY_TIPS_ERROR_NETWORK_UNUSABLE = "tips.error.network_unusable"

    private val KEY_TIPS_ERROR_INTERNAL = "tips.error.internal"

    private val KEY_TIPS_STATE_READY = "tips.state.ready"

    private val KEY_TIPS_STATE_WAIT = "tips.state.wait"

    private val KEY_TIPS_STATE_INITIALIZING = "tips.state.initializing"

    private val KEY_TIPS_STATE_LISTENING = "tips.state.listening"

    private val KEY_TIPS_STATE_RECOGNIZING = "tips.state.recognizing"

    private val KEY_TIPS_COPYRIGHT = "tips.copyright"

    private val KEY_TIPS_WAITNET = "tips.wait.net"

    private val KEY_BTN_DONE = "btn.done"

    private val KEY_BTN_CANCEL = "btn.cancel"

    private val KEY_BTN_RETRY = "btn.retry"

    private val KEY_TIPS_HELP_TITLE = "tips.help.title"

    private val KEY_BTN_START = "btn.start"

    private val KEY_BTN_HELP = "btn.help"

    private val KEY_TIPS_PREFIX = "tips.suggestion.prefix"
    private val ERROR_NETWORK_UNUSABLE = 0x90000

    private var mErrorCode: Int = 0

    // 国际化标识定义end
    // TOTO 更新最终地址
    private val mUrl = "http://developer.baidu.com/static/community/servers/voice/sdk.html"

    private var mErrorRes: CharSequence? = ""

    private var mContentRoot: View? = null

    private var mMainLayout: View? = null

    private var mErrorLayout: View? = null

    private var mTipsTextView: TextView? = null

    private var mWaitNetTextView: TextView? = null

    private var mCompleteTextView: TextView? = null

    private var mCancelTextView: TextView? = null

    private var mRetryTextView: TextView? = null

    private var mVoiceWaveView: SDKAnimationView? = null

    private var mErrorTipsTextView: TextView? = null

    private var mLogoText1: TextView? = null

    private var mLogoText2: TextView? = null

    private var mCancelBtn: ImageButton? = null

    private var mHelpBtn: ImageButton? = null

    private var mTitle: TextView? = null

    private var mHelpView: View? = null

    private var mTipsAdapter: TipsAdapter? = null

    /**
     * 动效下面的提示，3S不说话出现，文字在列表中随机出。出现后隐藏版权声明
     */
    private var mSuggestionTips: TextView? = null

    /**
     * 静音异常时的提示语
     */
    private var mSuggestionTips2: TextView? = null

    private var mRecognizingView: View? = null

    /**
     * 连续上屏控件
     */
    private var mInputEdit: EditText? = null

    /**
     * 识别中的进度条
     *
     * @author zhaopengfei04
     */
    private val BAR_ONEND = 0

    private val BAR_ONFINISH = 1

    private var mSDKProgressBar: SDKProgressBar? = null

    private var step = 0

    // 3秒不出识别结果，显示网络不稳定,15秒转到重试界面
    private var delayTime = 0

    // 当前活跃的引擎类型

    private val mEngineType = 0

    internal var mMessage = Message.obtain()

    private var mBg: Drawable? = null

    /**
     * “说完了”按钮背景
     */
    private val mButtonBg = StateListDrawable()

    /**
     * 左侧按钮背景
     */
    private val mLeftButtonBg = StateListDrawable()

    /**
     * 右侧按钮背景
     */
    private val mRightButtonBg = StateListDrawable()

    /**
     * 帮助按钮
     */
    private val mHelpButtonBg = StateListDrawable()

    /**
     * 按钮文字颜色
     */
    private var mButtonColor: ColorStateList? = null

    /**
     * 按钮文字颜色反色
     */
    private var mButtonReverseColor: ColorStateList? = null

    /**
     * 底部版本声明字体颜色
     */
    private var mCopyRightColor = 0

    /**
     * 状态提示字体 颜色
     */
    private var mStateTipsColor = 0

    /**
     * 错误提示字体颜色
     */
    private var mErrorTipsColor = 0

    private var mTheme = 0

    // 识别启动后间隔多长时间不说话出现提示，单位毫秒
    private val SHOW_SUGGESTION_INTERVAL: Long = 3000

    val THEME_BLUE_LIGHTBG = BaiduASRDialogTheme.THEME_BLUE_LIGHTBG

    val THEME_BLUE_DEEPBG = BaiduASRDialogTheme.THEME_BLUE_DEEPBG

    val THEME_RED_LIGHTBG = BaiduASRDialogTheme.THEME_RED_LIGHTBG

    val THEME_RED_DEEPBG = BaiduASRDialogTheme.THEME_RED_DEEPBG

    val THEME_GREEN_LIGHTBG = BaiduASRDialogTheme.THEME_GREEN_LIGHTBG

    val THEME_GREEN_DEEPBG = BaiduASRDialogTheme.THEME_GREEN_DEEPBG

    val THEME_ORANGE_LIGHTBG = BaiduASRDialogTheme.THEME_ORANGE_LIGHTBG

    val THEME_ORANGE_DEEPBG = BaiduASRDialogTheme.THEME_ORANGE_DEEPBG

    protected val ERROR_NONE = 0
    /**
     * 国际化文本资源
     */
    private var mLableRes: ResourceBundle? = null

    /**
     * 对话框主题，取值参考 [BaiduASRDialogTheme.THEME_BLUE_DEEPBG]等
     */
    val PARAM_DIALOG_THEME = "BaiduASRDigitalDialog_theme"

    /**
     * 对话框启动后展示引导提示，不启动识别
     */
    @Deprecated("")
    val PARAM_SHOW_TIPS_ON_START = "BaiduASRDigitalDialog_showTips"

    /**
     * 引擎启动后3秒没检测到语音，在动效下方随机出现一条提示语。在配置了提示语列表后，默认开启。
     */
    @Deprecated("")
    val PARAM_SHOW_TIP = "BaiduASRDigitalDialog_showTip"

    /**
     * 未检测到语音异常时，将“取消”按钮替换成帮助按钮。在配置了提示语列表后，默认开启。
     */
    @Deprecated("")
    val PARAM_SHOW_HELP_ON_SILENT = "BaiduASRDigitalDialog_showHelp"

    /**
     * 提示语列表。String数组
     */
    @Deprecated("")
    val PARAM_TIPS = "BaiduASRDigitalDialog_tips"

    private val mHandler = Handler()

    /**
     * 单条提示语前缀
     */
    private var mPrefix: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = intent

        if (params != null) {
            mTheme = params.getIntExtra(PARAM_DIALOG_THEME, mTheme)
        }
        initView()
        loadI18N()
        startRecognition()
        internalOnStart()
    }



    private fun initView() {
        initResources(mTheme)
        mContentRoot = View.inflate(this,
                resources.getIdentifier("bdspeech_digital_layout", "layout", packageName), null)
        if (mContentRoot != null) {
            mContentRoot!!.findViewWithTag<View>("bg_layout").setBackgroundDrawable(mBg)
            mTipsTextView = mContentRoot!!.findViewWithTag<View>("tips_text") as TextView
            mTipsTextView!!.setTextColor(mStateTipsColor)
            mWaitNetTextView = mContentRoot!!.findViewWithTag<View>("tips_wait_net") as TextView
            mWaitNetTextView!!.visibility = View.INVISIBLE
            mWaitNetTextView!!.setTextColor(mStateTipsColor)
            mLogoText1 = mContentRoot!!.findViewWithTag<View>("logo_1") as TextView
            mLogoText2 = mContentRoot!!.findViewWithTag<View>("logo_2") as TextView
            mLogoText1!!.setOnClickListener(mClickListener)
            mLogoText2!!.setOnClickListener(mClickListener)
            mLogoText1!!.setTextColor(mCopyRightColor)
            mLogoText2!!.setTextColor(mCopyRightColor)
            mSuggestionTips = mContentRoot!!.findViewWithTag<View>("suggestion_tips") as TextView
            mSuggestionTips!!.setTextColor(mCopyRightColor)
            mSuggestionTips2 = mContentRoot!!.findViewWithTag<View>("suggestion_tips_2") as TextView
            mSuggestionTips2!!.setTextColor(mCopyRightColor)
            // 进度条
            mSDKProgressBar = mContentRoot!!.findViewWithTag<View>("progress") as SDKProgressBar
            mSDKProgressBar!!.visibility = View.INVISIBLE
            mSDKProgressBar!!.setTheme(mTheme)
            mCompleteTextView = mContentRoot!!.findViewWithTag<View>("speak_complete") as TextView
            mCompleteTextView!!.setOnClickListener(mClickListener)
            mCompleteTextView!!.setBackgroundDrawable(mButtonBg)
            mCompleteTextView!!.setTextColor(mButtonReverseColor)

            mCancelTextView = mContentRoot!!.findViewWithTag<View>("cancel_text_btn") as TextView
            mCancelTextView!!.setOnClickListener(mClickListener)
            mCancelTextView!!.setBackgroundDrawable(mLeftButtonBg)
            mCancelTextView!!.setTextColor(mButtonColor)
            mRetryTextView = mContentRoot!!.findViewWithTag<View>("retry_text_btn") as TextView
            mRetryTextView!!.setOnClickListener(mClickListener)

            mRetryTextView!!.setBackgroundDrawable(mRightButtonBg)
            mRetryTextView!!.setTextColor(mButtonReverseColor)

            mErrorTipsTextView = mContentRoot!!.findViewWithTag<View>("error_tips") as TextView
            mErrorTipsTextView!!.setTextColor(mErrorTipsColor)
            val bgDrawable = resources.getDrawable(
                    resources.getIdentifier("bdspeech_close_v2", "drawable", packageName))
            mCancelBtn = mContentRoot!!.findViewWithTag<View>("cancel_btn") as ImageButton
            mCancelBtn!!.setOnClickListener(mClickListener)
            mCancelBtn!!.setImageDrawable(bgDrawable)
            mHelpBtn = mContentRoot!!.findViewWithTag<View>("help_btn") as ImageButton
            mHelpBtn!!.setOnClickListener(mClickListener)
            mHelpBtn!!.setImageDrawable(mHelpButtonBg)
            mErrorLayout = mContentRoot!!.findViewWithTag("error_reflect")
            var layoutParams = mErrorLayout!!
                    .layoutParams as RelativeLayout.LayoutParams
            // mContentRoot.findViewWithTag("main_reflect").setId(0x7f0c0087);
            // mContentRoot.findViewWithTag("main_reflect").setBackgroundColor(Color.RED);
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.dialog_linear)
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.dialog_linear)

            mVoiceWaveView = mContentRoot!!.findViewWithTag<View>("voicewave_view") as SDKAnimationView
            mVoiceWaveView!!.setThemeStyle(mTheme)
            mMainLayout = mContentRoot!!.findViewWithTag("main_reflect")
            mVoiceWaveView!!.visibility = View.INVISIBLE
            mRecognizingView = mContentRoot!!.findViewWithTag("recognizing_reflect")
            mHelpView = mContentRoot!!.findViewWithTag("help_reflect")
            layoutParams = mHelpView!!.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.dialog_linear)
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.dialog_linear)
            mTitle = mContentRoot!!.findViewWithTag<View>("help_title") as TextView
            mTitle!!.setTextColor(mStateTipsColor)
            val suggestions = mContentRoot!!.findViewWithTag<View>("suggestions_list") as ListView
            mTipsAdapter = TipsAdapter(this)
            mTipsAdapter!!.setNotifyOnChange(true)
            mTipsAdapter!!.setTextColor(mStateTipsColor)
            suggestions.adapter = mTipsAdapter
            mInputEdit = mContentRoot!!.findViewWithTag<View>("partial_text") as EditText
            mInputEdit!!.setTextColor(mStateTipsColor)
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            setContentView(View(this))
            val param = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addContentView(mContentRoot, param)
            //            setContentView(mContentRoot);
        }
        window.setBackgroundDrawable(ColorDrawable(0))

        // 设置主题色调，不如亮蓝、暗红、亮绿等
        adjustThemeColor()
    }

    /**
     * 根据选定的主题，设置色调
     */
    private fun adjustThemeColor() {
        var hue = 0f
        when (mTheme) {
            BaiduASRDialogTheme.THEME_BLUE_LIGHTBG -> hue = 0f
            BaiduASRDialogTheme.THEME_BLUE_DEEPBG -> hue = 0f
            BaiduASRDialogTheme.THEME_GREEN_LIGHTBG -> hue = -108f
            BaiduASRDialogTheme.THEME_GREEN_DEEPBG -> hue = -109f
            BaiduASRDialogTheme.THEME_RED_LIGHTBG -> hue = 148f
            BaiduASRDialogTheme.THEME_RED_DEEPBG -> hue = 151f
            BaiduASRDialogTheme.THEME_ORANGE_LIGHTBG -> hue = -178f
            BaiduASRDialogTheme.THEME_ORANGE_DEEPBG -> hue = -178f
            else -> {
            }
        }
        val cm = ColorMatrix()
        ColorFilterGenerator.adjustColor(cm, 0f, 0f, 0f, hue)
        val filter = ColorMatrixColorFilter(cm)
        mBg!!.colorFilter = filter
        mButtonBg.colorFilter = filter
        mLeftButtonBg.colorFilter = filter
        mRightButtonBg.colorFilter = filter
        mSDKProgressBar!!.setHsvFilter(filter)
        mVoiceWaveView!!.setHsvFilter(filter)
    }

    /**
     * 全屏显示提示列表
     */
    private fun showSuggestions() {
        mErrorLayout!!.visibility = View.INVISIBLE
        mMainLayout!!.visibility = View.VISIBLE
        mRecognizingView!!.visibility = View.INVISIBLE
        mHelpView!!.visibility = View.VISIBLE
        mCompleteTextView!!.text = getString(KEY_BTN_START)
        mCompleteTextView!!.isEnabled = true
        mHelpBtn!!.visibility = View.INVISIBLE
        mHandler.removeCallbacks(mShowSuggestionTip)
        cancleRecognition()
    }

    private val mRandom = Random()

    private val mShowSuggestionTip = Runnable { showSuggestionTips() }

    /**
     * 显示动效正文的提示
     */
    private fun showSuggestionTips() {
        val tips = mTipsAdapter!!.getItem(mRandom.nextInt(mTipsAdapter!!.count))
        mSuggestionTips!!.text = mPrefix!! + tips
        mSuggestionTips!!.visibility = View.VISIBLE
        mLogoText1!!.visibility = View.GONE
    }

    @SuppressLint("NewApi")
    protected fun internalOnStart() {
        mTipsAdapter!!.clear()
        val temp  = mParams.getStringArray(PARAM_TIPS) as Collection<String>
        if (temp != null) {
            if (SDK_INT >= HONEYCOMB) {
                mTipsAdapter!!.addAll(temp)
            } else {
                for (tip in temp!!) {
                    mTipsAdapter!!.add(tip)
                }
            }
        }
        var showTips = false
        if (mTipsAdapter!!.count > 0) {
            mHelpBtn!!.visibility = View.VISIBLE
            showTips = mParams.getBoolean(PARAM_SHOW_TIPS_ON_START, false)
        } else {
            mHelpBtn!!.visibility = View.INVISIBLE
        }
        if (showTips) {
            showSuggestions()
        }
    }

    /**
     * 加载国际化字符串，{[.initView]之后调用
     */
    private fun loadI18N() {
        try {
            mLableRes = ResourceBundle.getBundle("BaiduASRDigitalDialog")
            mLogoText1!!.text = getString(KEY_TIPS_COPYRIGHT)
            mLogoText2!!.text = getString(KEY_TIPS_COPYRIGHT)
            mRetryTextView!!.text = getString(KEY_BTN_RETRY)
            mTitle!!.text = getString(KEY_TIPS_HELP_TITLE)
            mPrefix = getString(KEY_TIPS_PREFIX)
        } catch (e: MissingResourceException) {
            Log.w(TAG, "loadI18N error", e)
        }

    }

    /**
     * 获取国际化字符串
     *
     * @param key
     * @return 资源不存在返回Null
     */
    private fun getString(key: String): String? {
        var label: String? = null
        if (mLableRes != null) {
            try {
                label = mLableRes!!.getString(key)
            } catch (e: Exception) {
                Log.w(TAG, "get internationalization error key:$key", e)
            }

        }
        return label
    }

    /**
     * 初始化资源，图片、颜色
     */
    private fun initResources(theme: Int) {
        val context = this
        // 配色方案选择
        val buttonRecognizingBgName: Int?
        val buttonNormalBgName = resources.getIdentifier("bdspeech_btn_normal", "drawable", packageName)
        val buttonPressedBgName = resources.getIdentifier("bdspeech_btn_pressed", "drawable", packageName)
        var leftButtonNormalBgName: Int? = null
        var leftButtonPressedBgName: Int? = null
        val rightButtonNormalBgName = resources.getIdentifier("bdspeech_right_normal", "drawable", packageName)
        val rightButtonPressedBgName = resources.getIdentifier("bdspeech_right_pressed", "drawable", packageName)
        var bgName: Int? = null


        // 按下、不可用、其它状态颜色
        val colors = IntArray(3)
        // 按下、不可用、其它状态颜色
        val colorsReverse = IntArray(3)
        if (BaiduASRDialogTheme.isDeepStyle(theme)) {
            bgName = resources.getIdentifier("bdspeech_digital_deep_bg", "drawable", packageName)
            leftButtonNormalBgName = resources.getIdentifier("bdspeech_left_deep_normal", "drawable", packageName)
            leftButtonPressedBgName = resources.getIdentifier("bdspeech_left_deep_pressed", "drawable", packageName)

            buttonRecognizingBgName = resources.getIdentifier("bdspeech_btn_recognizing_deep", "drawable", packageName)

            colors[0] = -0x1
            colors[1] = -0xb2b2b3
            colors[2] = -0x1
            colorsReverse[0] = -0x1
            colorsReverse[1] = -0xb2b2b3
            colorsReverse[2] = -0x1

            mCopyRightColor = -0xa1a1a0
            mStateTipsColor = -0x39393a
            mErrorTipsColor = -0x181819
            mHelpButtonBg.addState(intArrayOf(android.R.attr.state_pressed), resources.getDrawable(
                    resources.getIdentifier("bdspeech_help_pressed_deep", "drawable", packageName)))
            mHelpButtonBg.addState(intArrayOf(), resources.getDrawable(
                    resources.getIdentifier("bdspeech_help_deep", "drawable", packageName)))
        } else {
            bgName = resources.getIdentifier("bdspeech_digital_bg", "drawable", packageName)
            leftButtonNormalBgName = resources.getIdentifier("bdspeech_left_normal", "drawable", packageName)
            leftButtonPressedBgName = resources.getIdentifier("bdspeech_left_pressed", "drawable", packageName)

            buttonRecognizingBgName = resources.getIdentifier("bdspeech_btn_recognizing", "drawable", packageName)

            colors[0] = -0xb8b8b9
            colors[1] = -0x171718
            colors[2] = -0xb8b8b9
            colorsReverse[0] = -0x1
            colorsReverse[1] = -0x414142
            colorsReverse[2] = -0x1

            mCopyRightColor = -0x282829
            mStateTipsColor = -0x969697
            mErrorTipsColor = -0x959596
            mHelpButtonBg.addState(intArrayOf(android.R.attr.state_pressed), resources.getDrawable(
                    resources.getIdentifier("bdspeech_help_pressed_light", "drawable", packageName)))
            mHelpButtonBg.addState(intArrayOf(), resources.getDrawable(
                    resources.getIdentifier("bdspeech_help_light", "drawable", packageName)))
        }

        mBg = resources.getDrawable(bgName)
        mButtonBg.addState(intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled), resources.getDrawable(buttonPressedBgName))
        mButtonBg.addState(intArrayOf(-android.R.attr.state_enabled), resources.getDrawable(buttonRecognizingBgName))
        mButtonBg.addState(intArrayOf(),
                resources.getDrawable(buttonNormalBgName))
        mLeftButtonBg.addState(intArrayOf(android.R.attr.state_pressed), resources.getDrawable(leftButtonPressedBgName))
        mLeftButtonBg.addState(intArrayOf(),
                resources.getDrawable(leftButtonNormalBgName))
        mRightButtonBg.addState(intArrayOf(android.R.attr.state_pressed), resources.getDrawable(rightButtonPressedBgName))
        mRightButtonBg.addState(intArrayOf(),
                resources.getDrawable(rightButtonNormalBgName))
        val states = arrayOfNulls<IntArray>(3)
        states[0] = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        states[1] = intArrayOf(-android.R.attr.state_enabled)
        states[2] = IntArray(1)

        mButtonColor = ColorStateList(states, colors)
        mButtonReverseColor = ColorStateList(states, colorsReverse)

    }

    private fun stopRecognizingAnimation() {
        mVoiceWaveView!!.resetAnimation()
    }

    private fun startRecognizingAnimation() {
        mVoiceWaveView!!.startRecognizingAnimation()
    }

    private val mClickListener = View.OnClickListener { v ->
        if ("speak_complete" == v.tag) {
            val btntitle = mCompleteTextView!!.text.toString()
            if (btntitle == getString(KEY_BTN_START)) {
                step = 0

                // 3秒不出识别结果，显示网络不稳定,15秒转到重试界面
                delayTime = 0
                mSDKProgressBar!!.visibility = View.INVISIBLE
                startRecognition()
            } else if (btntitle == getString(KEY_BTN_DONE)) {
                if (status === STATUS_Speaking) {
                    speakFinish()
                    onEndOfSpeech()
                } else {
                    cancleRecognition()
                    onFinish(SpeechRecognizer.ERROR_NO_MATCH, 0)
                }
            }
        } else if ("cancel_text_btn" == v.tag) {
            val btntitle = mCancelTextView!!.text.toString()
            if (btntitle == getString(KEY_BTN_HELP)) {
                showSuggestions()
            } else {
                finish()
            }
        } else if ("retry_text_btn" == v.tag) {
            step = 0

            // 3秒不出识别结果，显示网络不稳定,15秒转到重试界面
            delayTime = 0
            mInputEdit!!.visibility = View.GONE
            mSDKProgressBar!!.visibility = View.INVISIBLE

            startRecognition()
        } else if ("cancel_btn" == v.tag) {
            finish()
        } else if ("help_btn" == v.tag) {
            showSuggestions()
        } else if ("logo_1" == v.tag || "logo_2" == v.tag) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mUrl))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                // 忽略
            }

        }
    }

    protected fun onRecognitionStart() {
        barHandler.removeMessages(BAR_ONFINISH)
        barHandler.removeMessages(BAR_ONEND)

        step = 0

        // 3秒不出识别结果，显示网络不稳定,15秒转到重试界面
        delayTime = 0
        mInputEdit!!.setText("")
        mInputEdit!!.visibility = View.INVISIBLE
        mVoiceWaveView!!.visibility = View.VISIBLE
        mVoiceWaveView!!.startInitializingAnimation()
        mTipsTextView!!.text = getString(KEY_TIPS_STATE_WAIT)
        mErrorLayout!!.visibility = View.INVISIBLE
        mMainLayout!!.visibility = View.VISIBLE
        mCompleteTextView!!.text = getString(KEY_TIPS_STATE_INITIALIZING)
        mCompleteTextView!!.isEnabled = false

        // mInputEdit.setVisibility(View.GONE);
        mTipsTextView!!.visibility = View.VISIBLE
        mSDKProgressBar!!.visibility = View.INVISIBLE
        mWaitNetTextView!!.visibility = View.INVISIBLE

        mRecognizingView!!.visibility = View.VISIBLE
        mHelpView!!.visibility = View.INVISIBLE
        if (mTipsAdapter!!.count > 0) {
            mHelpBtn!!.visibility = View.VISIBLE
        }
        mSuggestionTips!!.visibility = View.GONE
        mLogoText1!!.visibility = View.VISIBLE
    }

    override fun onPrepared() {

        mVoiceWaveView!!.startPreparingAnimation()
        if (TextUtils.isEmpty(mPrompt)) {
            mTipsTextView!!.text = getString(KEY_TIPS_STATE_READY)
        } else {
            mTipsTextView!!.text = mPrompt
        }
        mCompleteTextView!!.text = getString(KEY_BTN_DONE)
        mCompleteTextView!!.isEnabled = true
        mHandler.removeCallbacks(mShowSuggestionTip)
        if (mParams.getBoolean(PARAM_SHOW_TIP, true) && mTipsAdapter!!.count > 0) {
            mHandler.postDelayed(mShowSuggestionTip, SHOW_SUGGESTION_INTERVAL)
        }
    }

    override fun onBeginningOfSpeech() {

        mTipsTextView!!.text = getString(KEY_TIPS_STATE_LISTENING)
        mVoiceWaveView!!.startRecordingAnimation()
        mHandler.removeCallbacks(mShowSuggestionTip)
    }

    override fun onVolumeChanged(volume: Int) {
        mVoiceWaveView!!.setCurrentDBLevelMeter(volume.toFloat())
    }

    override fun onEndOfSpeech() {

        mTipsTextView!!.text = getString(KEY_TIPS_STATE_RECOGNIZING)
        mCompleteTextView!!.text = getString(KEY_TIPS_STATE_RECOGNIZING)
        mSDKProgressBar!!.visibility = View.VISIBLE

        barHandler.sendEmptyMessage(BAR_ONEND)
        mCompleteTextView!!.isEnabled = false
        startRecognizingAnimation()
    }

    override fun onFinish(errorType: Int, errorCode: Int) {

        mErrorCode = errorType

        barHandler.removeMessages(BAR_ONEND)
        barHandler.sendEmptyMessage(BAR_ONFINISH)
        mWaitNetTextView!!.visibility = View.INVISIBLE
        stopRecognizingAnimation()
        if (errorType != ERROR_NONE) {

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, String.format("onError:errorType %1\$d,errorCode %2\$d ", errorType,
                        errorCode))
            }
            barHandler.removeMessages(BAR_ONFINISH)
            var showHelpBtn = false
            mSuggestionTips2!!.visibility = View.GONE
            when (errorType) {
                SpeechRecognizer.ERROR_NO_MATCH -> mErrorRes = "没有匹配的识别结果"
                SpeechRecognizer.ERROR_AUDIO -> {
                    mErrorRes = "启动录音失败"
                    if (mTipsAdapter!!.count > 0) {
                        if (mParams.getBoolean(PARAM_SHOW_HELP_ON_SILENT, true)) {
                            showHelpBtn = true
                        }
                        if (mParams.getBoolean(PARAM_SHOW_TIP, true)) {
                            val tips = mTipsAdapter!!.getItem(mRandom.nextInt(mTipsAdapter!!
                                    .count))
                            mSuggestionTips2!!.text = mPrefix!! + tips
                            mSuggestionTips2!!.visibility = View.VISIBLE
                        }
                    }
                }
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> mErrorRes = getString(KEY_TIPS_ERROR_SILENT)
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                    val spanString = SpannableString("网络超时，再试一次")
                    val span = object : URLSpan("#") {
                        override fun onClick(widget: View) {
                            startRecognition()
                        }
                    }
                    spanString.setSpan(span, 5, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    mErrorRes = spanString
                }
                SpeechRecognizer.ERROR_NETWORK -> if (errorCode == ERROR_NETWORK_UNUSABLE) {
                    mErrorRes = getString(KEY_TIPS_ERROR_NETWORK_UNUSABLE)
                } else {
                    mErrorRes = getString(KEY_TIPS_ERROR_NETWORK)
                }
                SpeechRecognizer.ERROR_CLIENT -> mErrorRes = "客户端错误"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> mErrorRes = "权限不足，请检查设置"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> mErrorRes = "引擎忙"
                SpeechRecognizer.ERROR_SERVER -> mErrorRes = getString(KEY_TIPS_ERROR_DECODER)
                else -> mErrorRes = getString(KEY_TIPS_ERROR_INTERNAL)
            }
            mCancelTextView!!.text = getString(if (showHelpBtn) KEY_BTN_HELP else KEY_BTN_CANCEL)
            mWaitNetTextView!!.visibility = View.INVISIBLE
            mErrorTipsTextView!!.movementMethod = LinkMovementMethod.getInstance()
            mErrorTipsTextView!!.text = mErrorRes
            mErrorLayout!!.visibility = View.VISIBLE
            mMainLayout!!.visibility = View.INVISIBLE
            mHelpBtn!!.visibility = View.INVISIBLE
            mHandler.removeCallbacks(mShowSuggestionTip)
        }
        mVoiceWaveView!!.visibility = View.INVISIBLE
    }

    override fun onPartialResults(results: Array<String?>?) {

        if (results != null) {

            if (results != null && results.size > 0) {

                if (mInputEdit!!.visibility != View.VISIBLE) {

                    mInputEdit!!.visibility = View.VISIBLE
                    mWaitNetTextView!!.visibility = View.INVISIBLE
                    mTipsTextView!!.visibility = View.INVISIBLE
                }

                mInputEdit!!.setText(results[0])
                mInputEdit!!.setSelection(mInputEdit!!.text.length)
                delayTime = 0
            }
        }

    }


    private val ENGINE_TYPE_ONLINE = 0
    private val ENGINE_TYPE_OFFLINE = 1

    protected fun showEngineType(engineType: Int) {
        val engineTypeString: String
        when (engineType) {
            ENGINE_TYPE_OFFLINE -> {
                engineTypeString = "当前正在使用离线识别引擎"
                mSuggestionTips!!.text = engineTypeString
                mSuggestionTips!!.visibility = View.VISIBLE
                mLogoText1!!.visibility = View.GONE
            }
            ENGINE_TYPE_ONLINE -> {
                mSuggestionTips!!.visibility = View.GONE
                mLogoText1!!.visibility = View.VISIBLE
            }
            else -> {
            }
        }
    }

    /**
     * 进度条
     */
    internal var barHandler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {

            if (msg.what == BAR_ONEND) {
                run {
                    if (delayTime >= 3000) {
                        if (mInputEdit!!.visibility == View.VISIBLE) {
                            mInputEdit!!.visibility = View.INVISIBLE
                        }

                        mTipsTextView!!.visibility = View.INVISIBLE
                        // 仅在线时显示“网络不稳定”
                        if (mEngineType == ENGINE_TYPE_ONLINE) {
                            mWaitNetTextView!!.text = getString(KEY_TIPS_WAITNET)
                            mWaitNetTextView!!.visibility = View.VISIBLE
                        }
                    } else {
                        if (mInputEdit!!.visibility == View.VISIBLE) {
                            mTipsTextView!!.visibility = View.INVISIBLE
                            mWaitNetTextView!!.visibility = View.INVISIBLE
                        } else {
                            mTipsTextView!!.visibility = View.VISIBLE
                            mWaitNetTextView!!.visibility = View.INVISIBLE
                        }

                    }
                    mMessage.what = BAR_ONEND
                    if (step <= 30) {
                        delayTime = delayTime + 10
                        step = step + 1
                        this.sendEmptyMessageDelayed(BAR_ONEND, 10)
                    } else if (step < 60) {
                        delayTime = delayTime + 100
                        step = step + 1
                        this.sendEmptyMessageDelayed(BAR_ONEND, 100)
                    } else {

                        if (delayTime >= 15000) {
                            cancleRecognition()
                            onFinish(SpeechRecognizer.ERROR_NETWORK, ERROR_NETWORK_UNUSABLE)
                            step = 0
                            delayTime = 0
                            mSDKProgressBar!!.visibility = View.INVISIBLE

                            this.removeMessages(BAR_ONEND)

                        } else {
                            step = 60
                            delayTime = delayTime + 100
                            this.sendEmptyMessageDelayed(BAR_ONEND, 100)
                        }

                    }
                    mSDKProgressBar!!.setProgress(step)

                }
            } else if (msg.what == BAR_ONFINISH) {

                if (step <= 80) {
                    step = step + 3
                    this.sendEmptyMessageDelayed(BAR_ONFINISH, 1)
                } else {
                    step = 0
                    delayTime = 0
                    mInputEdit!!.visibility = View.GONE
                    mSDKProgressBar!!.visibility = View.INVISIBLE
                    if (mErrorCode == ERROR_NONE) {

                        finish()
                    }

                    this.removeMessages(BAR_ONFINISH)
                }
                mSDKProgressBar!!.setProgress(step)
            }
        }

    }
}