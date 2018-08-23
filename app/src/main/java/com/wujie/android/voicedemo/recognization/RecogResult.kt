package com.wujie.android.voicedemo.recognization

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by wujie on 2018/8/7/007.
 *
 */
class RecogResult {
    companion object {
        private val ERROR_NONE: Int = 0

        fun parseJson(jsonStr: String?): RecogResult {
            var result = RecogResult()
            result.origalJson = jsonStr
            try {
                val json = JSONObject(jsonStr)
                val error = json.optInt("error")
                result.error = error
                result.subError = json.optInt("sub_error")
                result.resultType = json.optString("result_type")
                if (error == ERROR_NONE) {
                    result.origalResult = json.optString("origin_result")
                    val arr = json.optJSONArray("results_recognition")
                    if(arr != null) {
                        val size = arr.length()
                        var recogs = arrayOfNulls<String>(size)

                        for (i in 0 until size) {
                            recogs[i] = arr.optString(i)
                        }
                        result.resultRecognition = recogs

                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return result
        }


    }

    var origalJson: String? = null

    var resultRecognition: Array<String?>? = null

    var origalResult: String? = null

    var sn: String? = null

    var desc: String? = null

    var resultType: String? = null

    var error: Int = -1

    var subError: Int = -1

    fun haseError() = error != ERROR_NONE

    fun isFinalResult() = "final_result" == resultType

    fun isPartialReusult() = "partial_result" == resultType

    fun isNluResult() = "nlu_result" == resultType

}