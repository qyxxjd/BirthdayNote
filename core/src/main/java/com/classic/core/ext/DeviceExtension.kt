@file:Suppress("unused")

package com.classic.core.ext

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import java.util.*

/**
 * Device info extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
fun uuid(): String = UUID.randomUUID().toString()

/** 设备品牌 */
fun brand(): String = Build.BRAND

/** 设备型号 */
fun model(): String = Build.MODEL

/** SDK版本 */
fun sdkVersion(): Int = Build.VERSION.SDK_INT

/** 用户可见的版本字符串 */
fun userVersion(): String = Build.VERSION.RELEASE

/** 硬件名称 */
fun hardware(): String = Build.HARDWARE

@SuppressLint("HardwareIds")
fun Context.androidId(): String {
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: ""
}

fun Context.id(): String {
    var id = androidId()
    if (id.isEmpty()) id = uuid()
    return brand() + "-" + model() + "-" + sdkVersion() + "-" + userVersion() + "-" + hardware() + "_" + id
}

private const val LINE = "\n"
/**
 * 设备摘要信息
 */
fun Context.deviceSummary(): String {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics().also {
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(it)
    }
    val size = screenSize()
    return "手机信息：$LINE" +
            "编号标签: ${Build.ID} $LINE" +
            "版 本 号: ${Build.DISPLAY} $LINE" +
            "手机品牌: ${Build.BRAND} $LINE" +
            "手机型号: ${Build.MODEL} $LINE" +
            "硬件名称: ${Build.HARDWARE} $LINE" +
            "类   型: ${Build.TYPE} $LINE" +
            "分 辨 率: ${size.width} x ${size.height} $LINE" +
            "屏幕密度: ${dm.densityDpi} $LINE" +
            "Android版本: ${Build.VERSION.RELEASE} $LINE" +
            "Android SDK: ${Build.VERSION.SDK_INT}"
}
