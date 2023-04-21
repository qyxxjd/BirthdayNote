@file:Suppress("unused")

package com.classic.core.ext

import android.content.Context
import android.os.Looper
import android.util.Log
import com.classic.core.util.ProcessUtil

/**
 * Thread extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */

/**
 * 是否是主进程
 */
fun Context.isMainProcess(): Boolean = ProcessUtil.getCurrentProcessName(this) == packageName

/** 当前线程是否为主线程 */
fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

fun printCurrentThread(tag: String = "") {
    Log.println(Log.DEBUG, tag, Thread.currentThread().toString())
}