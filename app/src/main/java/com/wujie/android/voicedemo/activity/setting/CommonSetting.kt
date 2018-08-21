package com.wujie.android.voicedemo.activity.setting

import android.os.Bundle
import android.preference.*

/**
 * Created by wujie on 2018/8/16/016.
 *
 */
abstract class CommonSetting : PreferenceActivity(), Preference.OnPreferenceChangeListener {

    protected var setting: Int = 0

    protected var title: String = "通用设置"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(title)
        this.addPreferencesFromResource(setting)
        val s = findPreference("root_screen") as PreferenceScreen
        bind(s)
    }

    private fun bind(group: PreferenceGroup) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        for (i in 0 until group.preferenceCount) {
            val p = group.getPreference(i) as Preference

            if (p is PreferenceGroup) {
                bind(p)
            } else {
                if (p !is CheckBoxPreference) {
                    val valli = sp.all[p.key]
                    p.summary = valli?.toString() ?: ""
                }
            }
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference !is CheckBoxPreference) {
            preference?.summary = newValue.toString()
        }
        return true
    }
}