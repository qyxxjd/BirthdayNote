@file:Suppress("unused")

package com.classic.core.ext

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.Spanned
import androidx.core.text.HtmlCompat
import java.net.URLEncoder
import java.util.regex.Pattern

/**
 * String extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
const val EMPTY = ""
const val KEY_ID = "id"
const val KEY_URL = "url"
const val KEY_NAME = "name"
const val KEY_TYPE = "type"
const val KEY_DETAIL = "detail"
const val KEY_TASK_ID = "taskId"
const val KEY_CONTENT = "content"
const val KEY_CHOOSE = "choose"
const val KEY_RESULT = "result"
const val KEY_SERIAL = "serial"
const val KEY_BOOLEAN = "boolean"

const val MONEY_SYMBOL = "¥"

/** 转换为非空字符串 */
fun String?.safeText(): String = this ?: ""
/** 字符串是否为空，包括字符串内容为`null` */
fun String?.isEmptyOrNullString(): Boolean = this.isNullOrEmpty() || "null".equals(this, true)
/** 是否为有效的手机号长度 */
fun String?.isPhoneLength(): Boolean = 11 == (this?.length ?: 0)
/** 字符串转Html格式的文本 */
fun String?.toHtmlColorText(colorString: String = "#F52525"): String {
    if (this.isNullOrEmpty()) return ""
    return "<font color=\"${colorString}\">${this}</font>"
}
fun String.toHtml(flag: Int? = null): Spanned {
    return HtmlCompat.fromHtml(this, flag ?: HtmlCompat.FROM_HTML_MODE_COMPACT)
}
/** Url编码 */
fun String?.encodeUrl(charset: String = "UTF-8"): String {
    if (this.isNullOrEmpty()) return ""
    return URLEncoder.encode(this, charset)
}

/** 复制文本到剪贴板 */
fun String.copyToClip(activity: Activity, hint: String = "复制成功"): Boolean {
    if (this.isEmpty()) return false
    val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    return try {
        val clipData = ClipData.newPlainText("text", this)
        clipboardManager?.setPrimaryClip(clipData)
        if (hint.isNotEmpty()) activity.toast(hint)
        true
    } catch (e: Exception) {
        false
    }
}
/** 字符串忽略大小写进行内容比较 */
fun String.eq(target: String?): Boolean {
    return target.equals(this, true)
}

/** 获取Http链接后面拼接的参数值 */
fun String?.getUrlParams(key: String, defaultValue: String = ""): String {
    if (this.isNullOrEmpty()) return defaultValue
    val uri = Uri.parse(this)
    return uri.getQueryParameter(key) ?: defaultValue
}
/** Http链接后面拼接多个参数 */
fun String?.appendUrlParams(vararg pairs: Pair<String, String>): String {
    if (this.isNullOrEmpty()) return ""
    val sb = StringBuilder(this)
    pairs.forEach {
        val keyString = "${it.first}="
        // 拼接参数前，先判断一下是否包含该参数，不包含时添加
        if (!this.contains(keyString)) {
            val delimiter = if (sb.contains("?")) "&" else "?"
            sb.append(delimiter).append(keyString).append(it.second)
        }
    }
    return sb.toString()
}

fun String?.withMaxLength(maxLength: Int, suffix: String = ""): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        if (this.length > maxLength) (this.substring(0, maxLength) + suffix) else this
    } catch (e: Exception) {
        this
    }
}

/** 格式化手机号，隐藏中间4位 */
fun String?.formatPhone(symbol: String = "****"): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        this.substring(0, 3) + symbol + this.substring(7)
    } catch (e: Exception) {
        this
    }
}

/**
 * 隐藏字符串中间内容
 *
 * @param prefixLength 前缀长度
 * @param suffixLength 后缀长度
 * @param symbol 隐藏部分替换内容
 */
fun String?.hideMiddle(prefixLength: Int, suffixLength: Int, symbol: String = "****"): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        this.substring(0, prefixLength) + symbol + this.substring(this.length - suffixLength)
    } catch (e: Exception) {
        this
    }
}

/**
 * 将文本中的所有手机号，中间4位替换为****或者指定的字符串
 */
fun String?.filterPhone(symbol: String = "****"): String {
    try {
        if (this.isNullOrEmpty()) return ""
        // 找到所有符合正则表达式的字符串
        var list = this.findPhone()
        var result: String
        if (list.isEmpty()) {
            // 如果没找到，替换所有空格后，再查找一遍
            result = this.replace(" ", "")
            list = result.findPhone()
        } else {
            result = this
        }
        list.forEach {
            result = result.replace(it, it.formatPhone(symbol))
        }
        return result
    } catch (e: Exception) {
        return this ?: ""
    }
}

/** 匹配Html标签的正则表达式 */
const val REGEX_HTML = "<(\\S*?)[^>]*>.*?|<.*? />"
/**
 * 使用正则表达式查找并替换指定文本
 */
fun String?.replaceMatcher(regex: String, newValue: String = EMPTY, ignoreCase: Boolean = false): String {
    if (this.isNullOrEmpty()) return EMPTY
    val pattern: Pattern = Pattern.compile(regex)
    var result: String = this
    val matcher = pattern.matcher(this)
    while (matcher.find()) {
        val text = matcher.group()
        result = result.replace(text, newValue, ignoreCase)
    }
    return result
}

/**
 * 使用正则表达式查找文本中的所有手机号
 */
fun String?.findPhone(pattern: Pattern = Pattern.compile("1\\d{10}")): MutableList<String> {
    val result = mutableListOf<String>()
    if (this.isNullOrEmpty()) return result
    val matcher = pattern.matcher(this)
    while (matcher.find()) {
        val text = matcher.group()
        result.add(text)
    }
    return result
}

/** 英文逗号 */
const val COMMA = ","
/** 中文逗号 */
const val COMMA_CHINESE = "，"
/**
 * 拆分逗号分隔的数据（这里兼容中文和英文逗号）
 */
fun String?.splitComma(): MutableList<String> {
    if (null == this || trim().isEmpty()) return mutableListOf()
    return when {
        this.indexOf(COMMA) != -1 -> this.split(COMMA).toMutableList()
        this.indexOf(COMMA_CHINESE) != -1 -> this.split(COMMA_CHINESE).toMutableList()
        else -> mutableListOf(this)
    }
}
/**
 * 将字符串列表转换为单个字符串，英文逗号分割
 */
fun Iterable<String>.joinComma(): String {
    return joinToString(separator = COMMA)
}
const val MIDDLE_LINE = "-"
/**
 * 拆分中横线分隔的数据（这里兼容中文和英文逗号）
 */
fun String?.splitMiddleLine(): MutableList<String> {
    if (null == this) return mutableListOf()
    return when {
        this.indexOf(MIDDLE_LINE) != -1 -> this.split(MIDDLE_LINE).toMutableList()
        else -> mutableListOf(this)
    }
}

/**
 * 安全数据转换 String -> Int
 *
 * @param defaultValue 转换异常时返回的默认值
 */
fun String?.safeConvertInt(defaultValue: Int = 0): Int {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        this.toInt()
    } catch (e: Exception) {
        defaultValue
    }
}
/**
 * 安全数据转换 String -> Long
 *
 * @param defaultValue 转换异常时返回的默认值
 */
fun String?.safeConvertLong(defaultValue: Long = 0): Long {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        this.toLong()
    } catch (e: Exception) {
        defaultValue
    }
}
/**
 * 安全数据转换 String -> Double
 *
 * @param defaultValue 转换异常时返回的默认值
 */
fun String?.safeConvertDouble(defaultValue: Double = 0.0): Double {
    if (this.isNullOrEmpty()) return defaultValue
    return try {
        this.toDouble()
    } catch (e: Exception) {
        defaultValue
    }
}
/**
 * 安全数据转换 Number -> String
 */
fun Number?.safeConvertString(): String {
    if (null == this) return "0"
    return this.toString()
}

fun String?.toSafeQuantity(): Int = this.safeConvertInt().toSafeQuantity()
fun Int?.toSafeQuantity(defaultValue: Int = 1): Int {
    if (null == this) return defaultValue
    return if (this < defaultValue) defaultValue else this
}

/** 如果网址是//开通，在前面追加https: */
fun String?.safeUrl(prefix: String = "https:"): String {
    if (this.isNullOrEmpty()) return ""
    if (this.startsWith("//", true)) return "$prefix$this"
    return this
}
/** 简易的网址合法校验 */
fun String?.hasHttpUrl(): Boolean {
    if (this.isNullOrEmpty()) return false
    return this.startsWith("http://", true) ||
            this.startsWith("https://", true)
}
/** 是否是svg动画地址 */
@Suppress("SpellCheckingInspection")
fun String?.hasSvgaAnimUrl(): Boolean {
    if (this.isNullOrEmpty()) return false
    return hasHttpUrl() && contains(".svga", true)
}

fun String?.isSafeUrl(): Boolean = hasHttpUrl()
fun String?.hasGif(): Boolean {
    if (this.isNullOrEmpty()) return false
    return this.endsWith(".gif", true)
}
/** 是否是颜色值 */
fun String?.hasColor(): Boolean {
    if (this.isNullOrEmpty()) return false
    // 普通颜色7位，带透明度的9位
    return startsWith("#", true) && (length == 7 || length == 9)
}
/** 字符串转颜色值 */
fun String?.toColor(defaultValue: Int = Color.WHITE): Int {
    if (this.isNullOrEmpty() || !startsWith("#")) return defaultValue
    return try {
        Color.parseColor(this)
    } catch (e: Exception) {
        defaultValue
    }
}