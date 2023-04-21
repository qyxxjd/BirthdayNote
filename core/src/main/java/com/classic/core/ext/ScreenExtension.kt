@file:Suppress("unused")

package com.classic.core.ext

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import timber.log.Timber
import java.lang.Integer.min
import kotlin.math.max

/**
 * Screen extensions
 *
 * @author Classic
 * @date 2021-03-03 15:48
 */
data class ScreenSize(val width: Int, val height: Int)

/**
 * 获取当前屏幕宽高
 */
fun Context.screenSize(): ScreenSize {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val bounds = windowManager.maximumWindowMetrics.bounds
        ScreenSize(bounds.width(), bounds.height())
    } else {
        // SDK30以前使用旧版API
        val metrics = DisplayMetrics().also {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(it)
        }
        ScreenSize(metrics.widthPixels, metrics.heightPixels)
    }
}

/**
 * 获取当前屏幕宽度
 */
fun Context.screenWidth(): Int = screenSize().width
/**
 * 获取当前屏幕高度
 */
fun Context.screenHeight(): Int = screenSize().height
/**
 * 获取当前屏幕宽高的最大值
 */
fun Context.screenMaxSize(): Int {
    val size = screenSize()
    return max(size.width, size.height)
}
/**
 * 获取当前屏幕宽高的最小值
 */
fun Context.screenMinSize(): Int {
    val size = screenSize()
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        min(size.width, size.height)
    } else {
        if (size.width < size.height) size.width else size.height
    }
}

fun Context.printScreenSize() {
    try {
        Timber.tag("ScreenSize")
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.maximumWindowMetrics.bounds.apply {
                Timber.d("left:" + left + " right:" + right + " top:" + top + " bottom:" + bottom + " width:" + width() + " height:" + height())
            }
            windowManager.currentWindowMetrics.bounds.apply {
                Timber.d("left:" + left + " right:" + right + " top:" + top + " bottom:" + bottom + " width:" + width() + " height:" + height())
            }
        }
        DisplayMetrics().also {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(it)
        }.apply {
            Timber.d("widthPixels:$widthPixels heightPixels:$heightPixels density:$density densityDpi:$densityDpi scaledDensity:$scaledDensity xdpi:$xdpi ydpi:$ydpi ")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}