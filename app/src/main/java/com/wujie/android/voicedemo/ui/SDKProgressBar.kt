package com.wujie.android.voicedemo.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by wujie on 2018/8/22/022.
 */
class SDKProgressBar : View {

    private var colors: IntArray? = null

    private var barX = 0

    private var barY = 0

    private var rectHeight = 0

    private var rectWidth = 0

    private var barLength = 0

    private var mProgress = 0

    private var paint: Paint? = null

    private var mHsvFilter: ColorFilter? = null

    constructor(context: Context) : super(context) {
        paint = Paint()
        initView()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        paint = Paint()
        initView()
    }

    private fun initView() {
        barX = left
        barY = top
    }

    fun setProgress(progress: Int) {
        mProgress = when {
            progress < 0 -> 0
            progress > 80 -> 80
            else -> progress
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        barLength = MeasureSpec.getSize(widthMeasureSpec)

        rectHeight = barLength / 80
        rectWidth = rectHeight
        setMeasuredDimension(barLength, rectHeight)
    }

    fun setHsvFilter(filter: ColorFilter) {
        mHsvFilter = filter
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint?.colorFilter = mHsvFilter
        for (i in 0..mProgress) {
            if (mProgress <= 12) {
                paint?.color = colors!![12 - (mProgress - i)]
            } else {
                if (i <= mProgress - 12) {
                    paint?.color = colors!![0]
                } else {
                    paint?.color = colors!![12 - (mProgress - i)]
                }
            }
            canvas?.drawRect((barX+rectWidth*i+i).toFloat(), barY.toFloat(), (barX+rectWidth*i+i+rectWidth).toFloat(),
                    (barY+rectHeight).toFloat(), paint)
        }
    }

    fun setTheme(theme: Int) {
        val isDeepStyle = BaiduASRDialogTheme.isDeepStyle(theme)
        colors = if (isDeepStyle) COLOR_BLUE_DEEPBG else COLOR_BLUE_LIGHTBG
    }


    companion object {
        private val COLOR_BLUE_LIGHTBG = intArrayOf(-0x481c02, -0x692a03, -0x7c3003, -0x893603, -0x9a3e04, -0x9e4205, -0xa34a06, -0xa85207, -0xad5908, -0xae600a, -0xb16c0e, -0xb3720e, -0xb5760f)

        private val COLOR_BLUE_DEEPBG = intArrayOf(-0xedcc9e, -0xeeba8b, -0xefb081, -0xf0a575, -0xf19766, -0xf18b5a, -0xf27f4e, -0xf37341, -0xf46a38, -0xf45e2c, -0xf55320, -0xf64713, -0xf64713)
    }

}