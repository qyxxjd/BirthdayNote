@file:Suppress("unused")

package com.classic.core.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.classic.core.R

/**
 * View extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */

/**
 * 视图显示状态，并且宽高大于0
 */
fun View.isValid(): Boolean {
    return isVisible && measuredWidth > 0 && measuredHeight > 0
}
fun View.visible() {
    visibility = View.VISIBLE
}
fun View.invisible() {
    visibility = View.INVISIBLE
}
fun View.gone() {
    visibility = View.GONE
}
fun View.disable() {
    isEnabled = false
}
fun View.enable() {
    isEnabled = true
}
fun View?.applyVisible(visible: Boolean) {
    this?.apply { if (visible) visible() else gone() }
}
fun View?.applyInvisible(visible: Boolean) {
    this?.apply { if (visible) visible() else invisible() }
}

/** 模拟父控件的点击 */
fun View?.applyPerformClick(itemView: View) {
    if (null == this) return
    setOnTouchListener { _, event ->
        if (MotionEvent.ACTION_UP == event.action) {
            //模拟父控件的点击
            itemView.performClick()
        }
        return@setOnTouchListener false
    }
}
/** View设置宽高 */
fun View?.applySize(width: Int? = null, height: Int? = null) {
    if (null == this) return
    if (null == width && null == height) return
    val lp = this.layoutParams ?: ViewGroup.LayoutParams(width ?: 0, height ?: 0)
    if (null != width) lp.width = width
    if (null != height) lp.height = height
    this.layoutParams = lp
}
/** View设置宽度 */
fun View?.applyWidth(width: Int) {
    if (null == this) return
    val lp = layoutParams
    lp.width = width
    this.layoutParams = lp
}
/** View设置高度 */
fun View?.applyHeight(height: Int) {
    if (null == this) return
    val lp = layoutParams
    lp.height = height
    this.layoutParams = lp
}
/** View设置边距 */
fun View?.applyMargin(left: Int, top: Int, right: Int, bottom: Int) {
    if (null == this) return
    val lp = layoutParams
    if (lp is ViewGroup.MarginLayoutParams) {
        lp.setMargins(left, top, right, bottom)
        this.layoutParams = lp
    }
}
/** View设置相同的边距 */
fun View?.applyEqualMargin(margin: Int) {
    applyMargin(margin, margin, margin, margin)
}

/**
 * View转Bitmap
 *
 * > 前台的UI View转Bitmap。不需要执行：measure、layout
 */
fun View.toBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    val bitmap = Bitmap.createBitmap(this.measuredWidth, this.measuredHeight, config)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

/**
 * View转Bitmap
 *
 * > 在后台动态生成视图，并保存的场景。需要执行：measure、layout
 */
fun View.toBitmapWithMeasureLayout(width: Int, height: Int, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    // if (width > 0 && height > 0) {
        this.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
    // }
    this.layout(0, 0, this.measuredWidth, this.measuredHeight)
    val bitmap = Bitmap.createBitmap(this.measuredWidth, this.measuredHeight, config)
    val canvas = Canvas(bitmap)
    val background = this.background
    background?.draw(canvas)
    this.draw(canvas)
    return bitmap
}
fun View.toBitmapWithMeasureLayout(widthDp: Float, heightDp: Float, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    return toBitmapWithMeasureLayout(widthDp.dp2px(), heightDp.dp2px(), config)
}

/**
 * 时间段内仅触发一次点击事件
 *
 * @param time 目标时间内，单位：ms
 */
fun singleClick(time: Long = 800, block: ((View) -> Unit)): View.OnClickListener {
    return View.OnClickListener { v ->
        val current = SystemClock.uptimeMillis()
        val lastClickTime = (v.getTag(R.id.click_timestamp) as? Long) ?: 0
        if (current - lastClickTime > time) {
            v.setTag(R.id.click_timestamp, current)
            block(v)
        }
    }
}
/**
 * 时间段内仅触发一次点击事件
 *
 * @param time 目标时间内，单位：ms
 */
fun View.onClick(time: Long = 800, block: ((View) -> Unit)) {
    setOnClickListener(singleClick(time, block))
}

/**
 * 判断view是否向上滑动到屏幕以外
 */
fun View.isSwipeUpTheScreenOutside(): Boolean {
    val rect = Rect()
    getLocalVisibleRect(rect)
    return rect.top < 0
}

/**
 * 消费掉长按事件，避免TextView设置ClickableSpan后长按闪退
 *
 * - [Android SDK bug](https://issuetracker.google.com/issues/37127697)
 */
fun View?.applyLongClick() {
    this?.setOnLongClickListener { true }
}