@file:Suppress("unused")

package com.classic.core.ext

import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * 常用单位
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
/**
 * GB转字节
 *
 * 1073741824
 */
const val GB_2_BYTE: Long = 1024 * 1024 * 1024
/**
 * MB转字节
 *
 * 1048576
 */
const val MB_2_BYTE: Long = 1024 * 1024
/**
 * KB转字节
 */
const val KB_2_BYTE: Long = 1024

/** 秒转毫秒 */
fun Long.seconds2ms(): Long = convert2ms(this, TimeUnit.SECONDS)
/** 分钟转毫秒 */
fun Long.minutes2ms(): Long = convert2ms(this, TimeUnit.MINUTES)
/** 小时转毫秒 */
fun Long.hours2ms(): Long = convert2ms(this, TimeUnit.HOURS)
/** 天转毫秒 */
fun Long.days2ms(): Long = convert2ms(this, TimeUnit.DAYS)
/** 自定义单位转毫秒 */
fun Long.convert2ms(v: Long, timeUnit: TimeUnit): Long = TimeUnit.MILLISECONDS.convert(v, timeUnit)

/**
 * 格式化文件大小
 */
fun Long.formatByte(scale: Int = 2): String {
    val df = DecimalFormat("#.00")
    return when {
        this >= GB_2_BYTE -> df.format(this.toDouble() / GB_2_BYTE).replace(scale) + "G"
        this >= MB_2_BYTE -> df.format(this.toDouble() / MB_2_BYTE).replace(scale) + "M"
        this >= KB_2_BYTE -> df.format(this.toDouble() / KB_2_BYTE).replace(scale) + "K"
        this <= 0 -> ""
        else -> df.format(this.toDouble()).replace(scale) + "B"
    }
}