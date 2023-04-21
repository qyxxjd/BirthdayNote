package com.classic.core.ext

import android.app.ActivityManager
import android.content.Context
import timber.log.Timber

/**
 * Application extensions
 *
 * @author LiuBin
 * @date 2022/3/28 14:28
 */

/**
 * 应用是否在前台
 *
 * - 在屏幕最前端,可获取焦点。`IMPORTANCE_FOREGROUND = 100`
 * - 在屏幕前端、获取不到焦点。`IMPORTANCE_VISIBLE = 200`
 * - 在服务中。`IMPORTANCE_SERVICE = 300`
 * - 后台。`IMPORTANCE_CACHED = 400`
 * - 空进程。`IMPORTANCE_EMPTY = 500`
 */
fun Context.isApplicationInForeground(): Boolean {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    am.runningAppProcesses?.forEach {
        if (packageName == it.processName) {
            val status = it.importance
            Timber.d("当前应用状态：$status")
            return ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == status ||
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE == status
        }
    } ?: Timber.d("当前没有正在运行的应用进程")
    return false
}

/**
 * 应用是否在后台
 */
fun Context.isApplicationInBackground(): Boolean = !isApplicationInForeground()