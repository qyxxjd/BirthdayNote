@file:Suppress("unused")

package com.classic.core.ext

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.RelativeSizeSpan
import android.widget.TextView
import com.classic.core.util.CenterImageSpan
import com.classic.core.util.RadiusBackgroundSpan

/**
 * TextView extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */

/**
 * 大于0时才显示
 */
fun TextView?.visibleGreaterThanZero(number: String?) {
    val v = number.safeConvertLong()
    this?.apply {
        text = number
        if (v > 0) visible() else gone()
    }
}

/** 添加：删除线-中横线 */
fun TextView.addDeleteLine() {
    val paintFlags = this.paintFlags
    this.paint.flags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}
/** 移除：删除线-中横线 */
fun TextView.removeDeleteLine() {
    val paintFlags = this.paintFlags
    this.paint.flags = paintFlags and (Paint.STRIKE_THRU_TEXT_FLAG.inv())
}
/** 添加：下划线 */
fun TextView.addUnderLine() {
    val paintFlags = this.paintFlags
    this.paint.flags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}
/** 移除：下划线 */
fun TextView.removeUnderLine() {
    val paintFlags = this.paintFlags
    this.paint.flags = paintFlags and (Paint.UNDERLINE_TEXT_FLAG.inv())
}
/** 文本添加Drawable */
fun TextView.applyDrawable(left: Drawable? = null, top: Drawable? = null,
    right: Drawable? = null, bottom: Drawable? = null) {
    applyDrawableBounds(left)
    applyDrawableBounds(top)
    applyDrawableBounds(right)
    applyDrawableBounds(bottom)
    setCompoundDrawables(left, top, right, bottom)
}
private fun applyDrawableBounds(drawable: Drawable?) {
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
}
/** 清空Drawable */
fun TextView.clearDrawable() {
    // setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    setCompoundDrawables(null, null, null, null)
}

/**
 * 使用Html内容
 */
fun TextView?.applyHtml(content: String?) {
    if (null != this && !content.isNullOrEmpty()) {
        this.text = content.toHtml()
    }
}

/**
 * 设置文本扩展属性
 */
fun TextView?.applyMovementMethod(method: MovementMethod = LinkMovementMethod.getInstance()) {
    this?.movementMethod = method
}

/**
 * 文本开头的位置添加居中显示的图标
 *
 * @param drawableResId 图标资源
 * @param text 文本内容
 */
fun TextView.applyImageSpan(drawableResId: Int, text: String) {
    val imageSpan = CenterImageSpan(context, drawableResId)
    val content = SpannableString("* $text")
    // content.setSpan(imageSpan, 0, 1, 0)
    content.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    setText(content)
}
/**
 * 文本开头的位置添加文字标签
 *
 * @param tag 文字标签
 * @param tagTextColor 文字标签颜色
 * @param tagBackgroundColor 背景颜色
 * @param tagBackgroundRadius 圆角
 * @param text 文本内容
 */
fun TextView.applyTextTagSpan(
    tag: String,
    tagTextColor: Int,
    tagBackgroundColor: Int,
    tagBackgroundRadius: Float,
    text: String) {
    val span = SpannableString("$tag $text")
    val backgroundColorSpan = RadiusBackgroundSpan(tagTextColor, tagBackgroundColor, tagBackgroundRadius)
    span.setSpan(backgroundColorSpan, 0, tag.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    span.setSpan(RelativeSizeSpan(0.8F), 0, tag.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    setText(span)
}

/**
 * 自定义字体
 *
 * @param fontPath 字体本地路径。"fonts/gift.ttf"
 */
fun TextView.applyFont(fontPath: String) {
    typeface = Typeface.createFromAsset(context.assets, fontPath)
}