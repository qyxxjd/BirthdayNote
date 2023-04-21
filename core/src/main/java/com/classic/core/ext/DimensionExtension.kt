@file:Suppress("unused")

package com.classic.core.ext

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.DimenRes
import kotlin.math.roundToInt

/**
 * Dimension extensions
 *
 * @author Classic
 * @date 2021/5/21 9:29 上午
 */

val Number.dp: Int
    get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()

val Number.sp: Float
    get() = toFloat() * Resources.getSystem().displayMetrics.scaledDensity + 0.5f

fun Context.px(@DimenRes dimenResId: Int): Int = resources.getDimensionPixelSize(dimenResId)

fun Context.dp2px(dpValue: Float): Float {
    val scale = resources.displayMetrics.density
    return dpValue * scale + 0.5f
}
fun Context.px2dp(pxValue: Float): Float {
    val scale = resources.displayMetrics.density
    return pxValue / scale + 0.5f
}

fun Float.dp2px(): Int {
    return TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
        .roundToInt()
}

fun Float.sp2px(): Int {
    return TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)
        .roundToInt()
}
