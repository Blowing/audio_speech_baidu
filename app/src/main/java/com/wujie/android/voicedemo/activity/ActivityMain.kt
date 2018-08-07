package com.wujie.android.voicedemo.activity

import android.app.ListActivity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import com.baidu.speech.asr.SpeechConstant

class ActivityMain : ListActivity() {

    val CATEGORY_SAMPLE_CODE = "com.baidu.speech.recognizerdemo.intent.category" +
            ".SAMPLE_CODE"
    val TAG = "ActivityMain"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().remove(SpeechConstant.IN_FILE).apply() //
        // infile参数用于控制识别一个PCM音频流（或文件），每次进入程序都将该值清除，以避免体验时没有使用录音的问题
        listAdapter = SimpleAdapter(this, getData(), android.R.layout.simple_list_item_1,
                arrayOf("title"), intArrayOf(android.R.id.text1))

    }

    private fun getData() : List<Map<String, Any>> {
        var myData = arrayListOf<Map<String, Any>>()
        var mainIntent = Intent(Intent.ACTION_MAIN,null)
        mainIntent.addCategory(CATEGORY_SAMPLE_CODE)
        var list: List<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, 0) ?: return myData
        list.forEach {
            if(packageName == it.activityInfo.packageName) {
                val labelSeq = it.loadLabel(packageManager)
                var description : CharSequence? = null
                val label = labelSeq?.toString() ?: it.activityInfo.name
                if (it.activityInfo.descriptionRes != 0) {
                    description = packageManager.getText(it.activityInfo
                            .packageName,
                            it.activityInfo.descriptionRes, null)
                }
                addItem(myData, label, activityIntent(it.activityInfo.packageName, it
                        .activityInfo.name), description)

            }
        }

        return myData
    }

    private fun addItem(data: ArrayList<Map<String, Any>>, label: String?,
                        activityIntent: Intent, description: CharSequence?) {
        var temp = hashMapOf<String, Any>()
        temp["title"] = label!!
        if (description != null) {
            temp.put("description", description)
        }
        temp["intent"] = activityIntent
        data.add(temp)


    }

    private fun activityIntent(pkg: String, componentName: String) : Intent{
        var intent = Intent()
        intent.putExtra("name1", componentName)
        intent.putExtra("name2", componentName)
        intent.setClassName(pkg, componentName)
        return intent
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val map: Map<*, *> = l?.getItemAtPosition(position) as Map<*, *>
        startActivity(map["intent"] as Intent?)
    }
}
