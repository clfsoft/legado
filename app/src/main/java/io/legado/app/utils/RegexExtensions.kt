package io.legado.app.utils

import android.util.Log
import com.google.re2j.Pattern
import com.script.SimpleBindings
import io.legado.app.constant.AppConst

/**
 * 带有超时检测的正则替换
 */
fun CharSequence.replaceRegex(regex: String, replacement: String): String {
    val charSequence = this
    val isJs = replacement.startsWith("@js:")
    val replacement1 = if (isJs) replacement.substring(4) else replacement
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(charSequence)
    val stringBuffer = StringBuffer()
    while (matcher.find()) {
        if (isJs) {
            val bindings = SimpleBindings()
            bindings["result"] = matcher.group()
            val jsResult =
                AppConst.SCRIPT_ENGINE.eval(replacement1, bindings).toString()
            matcher.appendReplacement(stringBuffer, jsResult)
        } else {
            matcher.appendReplacement(stringBuffer, replacement1)
        }
    }
    matcher.appendTail(stringBuffer)
    Log.e("regex", "end")
    return stringBuffer.toString()
}

