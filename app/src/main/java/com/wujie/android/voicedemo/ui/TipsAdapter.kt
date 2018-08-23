package com.wujie.android.voicedemo.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Created by wujie on 2018/8/22/022.
 */
class TipsAdapter : ArrayAdapter<String>{

    private var mTextColor = 0
    constructor(context: Context, textViewResourceId: Int): super(context, textViewResourceId) {}

    constructor(context: Context):this(context, 0) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val text: TextView

        val view = convertView ?: View.inflate(context, context.resources.getIdentifier
        ("bdspeech_suggestion_item", "layout", context.packageName), null)

        if (view is TextView){
            text = view
        } else {
            text = view.findViewWithTag("textView")
        }
        text.setTextColor(mTextColor)
        text.text = String.format(ITEM_FORMAT, position + 1, getItem(position))
        return view
    }

    fun setTextColor(color: Int) {
        mTextColor = color
    }

    private val ITEM_FORMAT = "%1\$d.\"%2\$s\""

}