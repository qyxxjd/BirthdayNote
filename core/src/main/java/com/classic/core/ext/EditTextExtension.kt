@file:Suppress("unused")

package com.classic.core.ext

import android.text.*
import android.text.InputFilter.LengthFilter
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import java.util.regex.Pattern

/**
 * EditText extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
/** 设置文本，并获取焦点，移动光标到文本的末尾 */
fun EditText?.applyText(fillText: String? = null) {
    if (null == this) return
    fillText?.let { if (it.isNotEmpty()) this.setText(it) }
    post {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        val content = text()
        if (content.isNotEmpty()) setSelection(content.length)
    }
}
/** 获取焦点 */
fun EditText?.applyFocus(delayMillis: Long = 200) {
    if (null == this) return
    postDelayed({
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        context.showKeyboard(this)
    }, delayMillis)
}

/** 设置输入框最大字符长度 */
fun EditText?.applyMaxLength(length: Int) {
    if (null == this) return
    val filterArray = arrayOfNulls<InputFilter>(1)
    filterArray[0] = LengthFilter(length)
    this.filters = filterArray
}

/** 清空文本 */
fun EditText?.clear() {
    this?.text = null
}

/** 获取输入框文本 */
fun EditText?.text(): String {
    if (this == null) return ""
    val editable = text
    if (null != editable) {
        return editable.toString().trim()
    }
    return ""
}
/** 输入框内容是否为空 */
fun EditText?.isEmpty(): Boolean = this.text().isEmpty()
/** 获取输入框数字 */
fun EditText?.float(): Float {
    if (this == null) return 0F
    val content = text()
    return try {
        content.toFloatOrNull() ?: 0F
    } catch (e: Exception) {
        0F
    }
}
/** 获取输入框数字 */
fun EditText?.double(): Double {
    if (this == null) return 0.0
    val content = text()
    return try {
        content.toDoubleOrNull() ?: 0.0
    } catch (e: Exception) {
        0.0
    }
}
/** 获取输入框数字 */
fun EditText?.int(): Int {
    if (this == null) return 0
    val content = text()
    return try {
        content.toIntOrNull() ?: 0
    } catch (e: Exception) {
        0
    }
}
/** 获取输入框数字 */
fun EditText?.long(): Long {
    if (this == null) return 0L
    val content = text()
    return try {
        content.toLongOrNull() ?: 0L
    } catch (e: Exception) {
        0L
    }
}

/**
 * 回车键搜索
 */
fun EditText?.applyEnterSearch(action: Int = EditorInfo.IME_ACTION_SEARCH, task: () -> Unit) {
    this?.setOnEditorActionListener { _, actionId, event ->
        if (action == actionId ||
            EditorInfo.IME_ACTION_UNSPECIFIED == actionId ||
            KeyEvent.KEYCODE_ENTER == (event?.keyCode ?: 0)) {
            task()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

/**
 * 文本输入框禁用回车键
 */
fun EditText?.disableEnter() {
    this?.setOnEditorActionListener { _, _, event ->
        return@setOnEditorActionListener KeyEvent.KEYCODE_ENTER == event.keyCode
    }
}

open class AbsTextWatcher: TextWatcher {
    override fun afterTextChanged(s: Editable) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}

/**
 * Android EditText 实现整数和小数位数限制
 *
 * @param lengthBefore 小数点前的位数
 * @param lengthAfter 小数点后的位数
 */
fun EditText?.addDecimalInputFilter(lengthBefore: Int, lengthAfter: Int) {
    this?.filters = arrayOf(DecimalDigitsInputFilter(lengthBefore, lengthAfter))
}
class DecimalDigitsInputFilter(lengthBefore: Int, lengthAfter: Int) : InputFilter {
    private var pattern: Pattern
    init {
        val regex = String.format("[0-9]{0,%d}+(\\.[0-9]{0,%d})?", lengthBefore, lengthAfter)
        pattern = Pattern.compile(regex)
    }

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        //直接输入"."返回"0."
        //".x"删除"x"输出为"."，inputFilter无法处理成"0."，所以只处理直接输入"."的case
        if ("." == source && "" == dest.toString()) {
            return "0."
        }
        val builder = StringBuilder()
        if (!dest.isNullOrEmpty()) builder.append(dest)
        if ("" == source) {
            builder.replace(dstart, dend, "")
        } else {
            builder.insert(dstart, source)
        }
        val resultTemp = builder.toString()
        //判断修改后的数字是否满足小数格式，不满足则返回 "",不允许修改
        val matcher = pattern.matcher(resultTemp)
        if (!matcher.matches()) {
            return ""
        }
        return null
    }
}

/**
 * 中文、字母、数字等类型输入过滤
 */
class CharInputFilter : InputFilter {
    //默认允许所有输入
    private var filterModel = 0xFF

    //限制输入的最大字符数, 小于0不限制
    private var maxInputLength = -1
    private var callbacks: MutableList<OnFilterCallback>? = null

    constructor(filterModel: Int) {
        this.filterModel = filterModel
    }

    constructor(filterModel: Int, maxInputLength: Int) {
        this.filterModel = filterModel
        this.maxInputLength = maxInputLength
    }

    fun setFilterModel(filterModel: Int) {
        this.filterModel = filterModel
    }

    fun setMaxInputLength(maxInputLength: Int) {
        this.maxInputLength = maxInputLength
    }

    /**
     * 将 dest 字符串中[dStart, dEnd] 位置对应的字符串, 替换成 source 字符串中 [start, end] 位置对应的字符串.
     */
    override fun filter(
        source: CharSequence,  //本次需要更新的字符串, (可以理解为输入法输入的字符,比如:我是文本)
        start: Int,  //取 source 字符串的开始位置,通常是0
        end: Int,  //取 source 字符串的结束位置,通常是source.length()
        dest: Spanned,  //原始字符串
        dStart: Int,  //原始字符串开始的位置,
        dEnd: Int //原始字符串结束的位置, 这种情况会在你已经选中了很多个字符, 然后用输入法输入字符的情况下.
    ): CharSequence {
        //此次操作后, 原来的字符数量
        val length = dest.length - (dEnd - dStart)
        if (maxInputLength > 0) {
            if (length == maxInputLength) {
                return ""
            }
        }
        val modification = SpannableStringBuilder()
        for (i in start until end) {
            val c = source[i]
            var append = false
            if (filterModel and MODEL_CHINESE == MODEL_CHINESE) {
                append = c.isChineseText() || append
            }
            if (filterModel and MODEL_CHAR_LETTER == MODEL_CHAR_LETTER) {
                append = c.isCharLetter() || append
            }
            if (filterModel and MODEL_NUMBER == MODEL_NUMBER) {
                append = c.isNumber() || append
            }
            if (filterModel and MODEL_ASCII_CHAR == MODEL_ASCII_CHAR) {
                append = c.isAsciiChar() || append
            }
            if (callbacks != null && filterModel and MODEL_CALLBACK == MODEL_CALLBACK) {
                for (callback in callbacks!!) {
                    append = callback.onFilterAllow(source, c, i, dest, dStart, dEnd) || append
                }
            }
            if (append) {
                modification.append(c)
            }
        }
        if (maxInputLength > 0) {
            val newLength = length + modification.length
            if (newLength > maxInputLength) {
                //越界
                modification.delete(maxInputLength - length, modification.length)
            }
        }
        return modification //返回修改后, 允许输入的字符串. 返回null, 由系统处理.
    }

    fun addFilterCallback(callback: OnFilterCallback) {
        filterModel = filterModel or MODEL_CALLBACK
        if (callbacks == null) {
            callbacks = ArrayList()
        }
        if (!callbacks!!.contains(callback)) {
            callbacks!!.add(callback)
        }
    }

    interface OnFilterCallback {
        /**
         * 是否允许输入字符c
         */
        fun onFilterAllow(
            source: CharSequence?,
            c: Char,
            cIndex: Int,
            dest: Spanned?,
            dStart: Int,
            dEnd: Int
        ): Boolean
    }

    companion object {
        //允许中文输入
        const val MODEL_CHINESE = 1

        //允许输入大小写字母
        const val MODEL_CHAR_LETTER = 2

        //允许输入数字
        const val MODEL_NUMBER = 4

        //允许输入Ascii码表的[33-126]的字符
        const val MODEL_ASCII_CHAR = 8

        //callback过滤模式
        const val MODEL_CALLBACK = 16
    }
}