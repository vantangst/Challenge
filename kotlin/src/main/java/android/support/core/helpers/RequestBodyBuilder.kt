package android.support.core.helpers

import android.util.Log

class RequestBodyBuilder {
    private val mRequest = hashMapOf<String, String>()

    fun put(field: String, value: String): RequestBodyBuilder {
        mRequest[field] = value
        Log.e(field, value)
        return this
    }

    fun build(): Map<String, String> {
        return mRequest
    }

    fun buildQuery(): String {
        val stringBuilder = StringBuilder()
        for (field in mRequest.keys) {
            stringBuilder.append(field)
                .append("=")
                .append(mRequest[field])
                .append("&")
        }
        if (stringBuilder.isNotEmpty())
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
        return stringBuilder.toString()
    }
}
