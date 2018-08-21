package com.wujie.android.voicedemo.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

/**
 * Created by wujie on 2018/8/20/020.
 */
class SDKAnimationView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        val SAMPE_RATE_VOLUME = 50

        val NO_ANIMATION_STATE = 0

        val INITIALIZING_ANIMATION_STATE = 4

        val PREPARING_ANIMATION_STATE = 1
        val RECORDING_ANIMATION_STATE = 2
        val RECOGNIZING_ANIMATION_STATE = 3

        private val PREPARING_BAIDU_LOGO_TIME = 1200
        private val RECOGNIZING_WAVE_TRANSLATION_TIME = 20

        // 控件行列分格数
        private val RECT_IN_ROW = 69
        private val RECT_IN_COLUMN = 21

        private val RECOGNIZING_SCANLINE_SHADOW_NUMBER = 5

        // 使音量下降效果变平和
        private val BAR_DROPOFF_STEP = 1

        // 百度logo阵列
        private val BAIDU_LOGO = intArrayOf(0x00003800, 0x00007C00, 0x00007CF8, 0x000039FC, 0x0003839C, 0x0007C76C, 0x0007CF6C, 0x00039C08, 0x00001FF8, 0x00039F18, 0x0007CFEC, 0x0007C7EC, 0x0003830C, 0x000039FC, 0x00007CF8, 0x00007C00, 0x00003800)
        private val BEGIN_LOC_X = 27

        /* INITIALIZING状态数据 */
        private val INIT_VOLUME_ARRAY = ByteArray(RECT_IN_ROW)

        /* PREPARING状态数据 */
        private val PREPARING_VOLUME_ARRAY = byteArrayOf(11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11)

        /* RECORDING状态数据 */
        /* 第一组值 */
        private val GROUP1_VOLUME_ARRAY1 = byteArrayOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3)
        private val GROUP1_VOLUME_ARRAY2 = byteArrayOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3)
        private val GROUP1_VOLUME_ARRAY3 = byteArrayOf(4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 5, 5, 5, 5, 6, 6, 6, 6, 5, 5, 4, 4, 4, 5, 5, 5, 6, 6, 7, 8, 8, 8, 9, 9, 9, 9, 9, 8, 8, 8, 7, 6, 6, 5, 5, 5, 4, 4, 4, 5, 5, 6, 6, 6, 6, 5, 5, 5, 5, 6, 6, 6, 5, 5, 5, 4, 4, 4, 4)
        private val GROUP1_VOLUME_ARRAY4 = byteArrayOf(7, 8, 8, 7, 7, 7, 8, 8, 9, 9, 9, 8, 8, 7, 6, 6, 5, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 9, 10, 10, 11, 11, 11, 12, 12, 12, 11, 11, 11, 10, 10, 9, 8, 8, 7, 7, 6, 6, 6, 5, 5, 5, 5, 6, 6, 7, 8, 8, 9, 9, 9, 8, 8, 7, 7, 7, 8, 8, 7)
        private val GROUP1_VOLUME_ARRAY5 = byteArrayOf(9, 9, 9, 10, 10, 11, 11, 12, 12, 12, 12, 11, 11, 10, 10, 9, 9, 9, 8, 8, 8, 8, 9, 9, 9, 10, 10, 11, 12, 12, 13, 13, 14, 14, 14, 14, 14, 13, 13, 12, 12, 11, 10, 10, 9, 9, 9, 8, 8, 8, 8, 9, 9, 9, 10, 10, 11, 11, 12, 12, 12, 12, 11, 11, 10, 10, 9, 9, 9)
        private val GROUP1_VOLUME_ARRAY6 = byteArrayOf(11, 11, 11, 12, 12, 13, 13, 14, 14, 14, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12, 12, 12, 13, 13, 13, 14, 14, 15, 15, 15, 16, 16, 16, 16, 16, 15, 15, 15, 14, 14, 13, 13, 13, 12, 12, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 15, 14, 14, 14, 13, 13, 12, 12, 11, 11, 11)
        private val GROUP1_VOLUME_ARRAY7 = byteArrayOf(13, 13, 14, 14, 15, 15, 16, 16, 16, 17, 17, 17, 16, 16, 16, 15, 15, 15, 14, 14, 14, 14, 15, 15, 15, 16, 16, 17, 17, 18, 18, 18, 19, 19, 19, 19, 19, 18, 18, 18, 17, 17, 16, 16, 15, 15, 15, 14, 14, 14, 14, 15, 15, 15, 16, 16, 16, 17, 17, 17, 16, 16, 16, 15, 15, 14, 14, 13, 13)
        /* 第二组值 */
        private val GROUP2_VOLUME_ARRAY1 = byteArrayOf(3, 3, 3, 3, 4, 4, 4, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 4, 4, 4, 3, 3, 3, 3)
        private val GROUP2_VOLUME_ARRAY2 = byteArrayOf(3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3)
        private val GROUP2_VOLUME_ARRAY3 = byteArrayOf(5, 5, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 9, 9, 9, 8, 8, 7, 7, 6, 6, 5, 5, 5, 4, 4, 4, 5, 5, 6, 6, 6, 5, 5, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 9, 9, 9, 8, 8, 7, 7, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3)
        private val GROUP2_VOLUME_ARRAY4 = byteArrayOf(5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 9, 10, 10, 11, 11, 11, 12, 12, 12, 11, 11, 11, 10, 10, 9, 8, 8, 7, 7, 6, 6, 6, 5, 5, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 9, 10, 10, 11, 11, 11, 12, 12, 12, 11, 11, 11, 10, 10, 9, 8, 8, 7, 7, 6, 6, 6, 5, 5, 5)
        private val GROUP2_VOLUME_ARRAY5 = byteArrayOf(9, 9, 8, 8, 8, 8, 9, 9, 9, 10, 10, 11, 12, 12, 13, 13, 14, 14, 14, 14, 14, 13, 13, 12, 12, 11, 11, 10, 10, 9, 9, 9, 8, 8, 8, 8, 8, 9, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 14, 14, 14, 13, 13, 12, 12, 11, 10, 10, 9, 9, 9, 8, 8, 8, 8, 9, 9)
        private val GROUP2_VOLUME_ARRAY6 = byteArrayOf(13, 13, 13, 13, 14, 14, 14, 13, 13, 13, 13, 13, 14, 14, 15, 15, 15, 16, 16, 16, 16, 16, 15, 15, 15, 14, 14, 14, 13, 13, 13, 13, 12, 12, 12, 12, 12, 13, 13, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 16, 16, 15, 15, 15, 14, 14, 13, 13, 13, 13, 13, 14, 14, 14, 13, 13, 13, 13)
        private val GROUP2_VOLUME_ARRAY7 = byteArrayOf(15, 15, 14, 14, 14, 14, 15, 15, 15, 16, 16, 17, 17, 18, 18, 18, 19, 19, 19, 19, 19, 18, 18, 18, 17, 17, 16, 16, 15, 15, 15, 14, 14, 14, 14, 14, 14, 14, 15, 15, 15, 16, 16, 17, 17, 18, 18, 18, 19, 19, 19, 19, 19, 18, 18, 18, 17, 17, 16, 16, 15, 15, 15, 14, 14, 14, 14, 15, 15)
        /* 第三组值 */
        private val GROUP3_VOLUME_ARRAY1 = byteArrayOf(3, 3, 3, 4, 4, 4, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 4, 4, 4, 3, 3, 3)
        private val GROUP3_VOLUME_ARRAY2 = byteArrayOf(3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 4, 4, 4, 5, 5, 6, 6, 6, 7, 7, 7, 7, 7, 6, 6, 6, 5, 5, 4, 4, 4, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3)
        private val GROUP3_VOLUME_ARRAY3 = byteArrayOf(5, 5, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 8, 8, 8, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 7, 7, 7, 8, 8, 9, 9, 9, 9, 9, 8, 8, 7, 7, 7, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 8, 8, 8, 7, 7, 6, 6, 5, 5, 5, 4, 4, 4, 5, 5)
        private val GROUP3_VOLUME_ARRAY4 = byteArrayOf(6, 6, 6, 7, 7, 8, 9, 9, 10, 10, 10, 11, 11, 11, 10, 10, 10, 9, 9, 8, 8, 7, 7, 7, 7, 8, 8, 9, 10, 10, 11, 11, 11, 12, 12, 12, 11, 11, 11, 10, 10, 9, 8, 8, 7, 7, 7, 7, 8, 8, 9, 9, 10, 10, 10, 11, 11, 11, 10, 10, 10, 9, 9, 8, 7, 7, 6, 6, 6)
        private val GROUP3_VOLUME_ARRAY5 = byteArrayOf(8, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 13, 13, 12, 12, 11, 11, 10, 10, 9, 9, 9, 9, 10, 10, 11, 12, 12, 13, 13, 14, 14, 14, 14, 14, 13, 13, 12, 12, 11, 10, 10, 9, 9, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 13, 13, 12, 12, 11, 11, 10, 10, 9, 9, 8, 8, 8)
        private val GROUP3_VOLUME_ARRAY6 = byteArrayOf(11, 11, 11, 11, 11, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 16, 16, 15, 15, 15, 14, 14, 14, 13, 13, 13, 13, 14, 14, 14, 15, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12, 11, 11, 11, 11, 11)
        private val GROUP3_VOLUME_ARRAY7 = byteArrayOf(14, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 18, 18, 17, 17, 16, 16, 15, 15, 14, 14, 14, 15, 15, 15, 16, 16, 17, 17, 18, 18, 18, 19, 19, 19, 19, 19, 18, 18, 18, 17, 17, 16, 16, 15, 15, 15, 14, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 18, 18, 17, 17, 16, 16, 15, 15, 14, 14, 14)

        private val VOLUMES_GROUP1 = arrayOf(GROUP1_VOLUME_ARRAY1, GROUP1_VOLUME_ARRAY2, GROUP1_VOLUME_ARRAY3, GROUP1_VOLUME_ARRAY4, GROUP1_VOLUME_ARRAY5, GROUP1_VOLUME_ARRAY6, GROUP1_VOLUME_ARRAY7)

        private val VOLUMES_GROUP2 = arrayOf(GROUP2_VOLUME_ARRAY1, GROUP2_VOLUME_ARRAY2, GROUP2_VOLUME_ARRAY3, GROUP2_VOLUME_ARRAY4, GROUP2_VOLUME_ARRAY5, GROUP2_VOLUME_ARRAY6, GROUP2_VOLUME_ARRAY7)

        private val VOLUMES_GROUP3 = arrayOf(GROUP3_VOLUME_ARRAY1, GROUP3_VOLUME_ARRAY2, GROUP3_VOLUME_ARRAY3, GROUP3_VOLUME_ARRAY4, GROUP3_VOLUME_ARRAY5, GROUP3_VOLUME_ARRAY6, GROUP3_VOLUME_ARRAY7)

    }

    private var volumes: Array<ByteArray>? = null


    internal var mBgColor = 0

    // 当前音量数据和目标音量数据，中间会进行插值动画
    private var currentVolumeArray = ByteArray(RECT_IN_ROW)
    private var targetVolumeArray = ByteArray(RECT_IN_ROW)

    private var sampleSideLength = 0.0

    private var mWidth = 0

    // 动画当前所处状态
    private var mAnimationState = -1

    // preparing状态下记录开始时间
    private var mPreparingBeginTime: Long = 0
    // recording状态下每帧动画间插值计算使用的时间
    private var mRecordingInterpolationTime: Long = 0
    // recording状态下每帧动画间插值计算当前的时间
    private var mRecordingCurrentTime: Long = 0
    // recognizing状态下记录开始时间
    private var mRecognizingBeginTime: Long = 0

    // recognizing下波动位置记录
    private var mRecognizingWaveIndex: Int = 0
    // recognizing下记录上行扫描和下行扫描状态
    private var mRecognizingRefreshCount: Int = 0

    private var mGriddingPaint: Paint? = null
    private var mBaiduLogePaint: Paint? = null
    private var mVolumnCeilingPaint: Paint? = null
    private var mVolumnShadowPaint: Paint? = null
    private var mLogoReversePaint: Paint? = null

    private var mMask: Drawable? = null

    private var mCurrentBar: Int = 0

    private var mVolumeCeilingColor1: Int = 0
    private var mVolumeCeilingColor2: Int = 0

    private var mVolumeShadowColor1: Int = 0
    private var mVolumeShadowColor2: Int = 0

    private var mRecognizingLineShadowColor1: Int = 0
    private var mRecognizingLineShadowColor2: Int = 0

    /**
     * 旋转view色相的画笔
     */
    private var mHsvFilterPaint: Paint? = null
    /**
     * 旋转view色相的缓存图片
     */
    private var mHsvFilterBitmap: Bitmap? = null
    /**
     * 旋转view色相的缓存画布
     */
    private var mHsvFilterCanvas: Canvas? = null
    /**
     * 要为画笔设置的过滤器，用于旋转view的色相
     */
    private var mHsvFilter: ColorFilter? = null
    private var mCurrentDBLevelMeter: Float = 0f

    init {
        mGriddingPaint = Paint()
        mGriddingPaint?.strokeWidth = 1f
        mBaiduLogePaint = Paint()
        mLogoReversePaint = Paint()
        mVolumnCeilingPaint = Paint()
        mVolumnShadowPaint = Paint()
        volumes = VOLUMES_GROUP1
        currentVolumeArray = volumes!![0]
        targetVolumeArray = volumes!![0]
        setThemeStyle(BaiduASRDialogTheme.THEME_BLUE_LIGHTBG)
    }

    private fun setThemeStyle(themeStyle: Int) {
        val isDeepStyle = BaiduASRDialogTheme.isDeepStyle(themeStyle)

        mBgColor = if (isDeepStyle) -0xe2e2e3 else -0x9090a

        mGriddingPaint?.color = if (isDeepStyle) -0xe2e2e3 else -0x1
        mBaiduLogePaint?.color = (if (isDeepStyle) -0xd0d0d1 else -0x131314)
        mLogoReversePaint?.color = (if (isDeepStyle) -0xfbc476 else -0x250f01)

        mVolumeCeilingColor1 = if (isDeepStyle) -0xba7d13 else -0x7a4f07
        mVolumeCeilingColor2 = if (isDeepStyle) 0x004582ED else -0x65240e

        mVolumeShadowColor1 = if (isDeepStyle) -0xfac276 else -0x311401
        mVolumeShadowColor2 = if (isDeepStyle) 0x00053D8A else -0x1

        mRecognizingLineShadowColor1 = if (isDeepStyle) -0xfac276 else -0x321501
        mRecognizingLineShadowColor2 = if (isDeepStyle) 0x00053D8A else 0x00CDEAFF

        mMask = resources.getDrawable(if (isDeepStyle)
            context.resources
                    .getIdentifier("bdspeech_mask_deep", "drawable", context.packageName)
        else
            context.resources
                    .getIdentifier("bdspeech_mask_light", "drawable", context.packageName))
    }

    fun startInitializingAnimation() {
        mAnimationState = INITIALIZING_ANIMATION_STATE
        mPreparingBeginTime = System.currentTimeMillis()

        removeCallbacks(mInvalidateTask)
        removeCallbacks(mRecordingUpdateTask)
        post(mInvalidateTask)
    }


    private val mRecordingUpdateTask = object : Runnable {
        override fun run() {
            val minBar = 0
            val maxBar = 6
            var bar = minBar
            bar += ((maxBar - minBar) * getCurrentDBLevelMeter() / 100).toInt()

            if (bar > mCurrentBar) {
                mCurrentBar = bar
            } else {
                mCurrentBar = Math.max(bar, mCurrentBar - BAR_DROPOFF_STEP)
            }

            mCurrentBar = Math.min(maxBar, mCurrentBar)

            // 初始音量情况下增加波动效果
            if (mCurrentBar == 0 && (Math.random() * 4).toInt() == 0) {
                mCurrentBar = 1
            }
            setVolumeLevel(mCurrentBar)
            removeCallbacks(mRecordingUpdateTask)
            postDelayed(mRecordingUpdateTask, SAMPE_RATE_VOLUME.toLong())
        }
    }

    private val mInvalidateTask = object : Runnable {
        override fun run() {
            invalidate()
            post(this)
        }
    }
}