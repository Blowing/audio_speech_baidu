package com.wujie.android.voicedemo.recognization

import com.baidu.speech.EventListener

class RecogEventAdapter(private val recogListener: IRecogListener) : EventListener{
    override fun onEvent(p0: String?, p1: String?, p2: ByteArray?, p3: Int, p4: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
