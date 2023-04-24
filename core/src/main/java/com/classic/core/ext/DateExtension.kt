@file:Suppress("unused")

package com.classic.core.ext

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

/**
 * Date extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
const val PATTERN_DATE = "yyyy-MM-dd"
const val PATTERN_DATE_POINT = "yyyy.MM.dd"
const val PATTERN_DATE_SIMPLE = "yyyyMMdd"
const val PATTERN_TIME = "HH:mm:ss"
const val PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss"
const val PATTERN_DATE_TIME_SHORT = "yyyy-MM-dd HH:mm"
const val PATTERN_DATE_TIME_POINT = "yyyy.MM.dd HH:mm"
const val PATTERN_YEAR_MONTH = "yyyyMM"
const val PATTERN_MONTH_DAY = "MM-dd"
const val PATTERN_HOUR_MINUTE = "HH:mm"
@Suppress("SpellCheckingInspection")
const val PATTERN_DATE_TIME_SIMPLE = "yyyyMMddHHmmss"

fun calendar(time: Long = System.currentTimeMillis()): Calendar = Calendar.getInstance().apply { timeInMillis = time }
fun calendar(date: Date): Calendar = Calendar.getInstance().apply { time = date }
fun calendar(year: Int, month: Int, day: Int): Calendar {
    val calendar = calendar()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    return calendar
}

fun Calendar.year(): Int = get(Calendar.YEAR)
/** Month(0-11) */
fun Calendar.month(): Int = get(Calendar.MONTH)
fun Calendar.day(): Int = get(Calendar.DAY_OF_MONTH)
fun Calendar.dayOfYear(): Int = get(Calendar.DAY_OF_YEAR)
fun Calendar.hour(): Int = get(Calendar.HOUR_OF_DAY)
fun Calendar.minute(): Int = get(Calendar.MINUTE)
fun Calendar.second(): Int = get(Calendar.SECOND)
fun Calendar.week(): Int = get(Calendar.DAY_OF_WEEK)

/**
 * 格式化日期
 *
 * @param pattern 时间格式模板
 */
fun Calendar.format(pattern: String = PATTERN_DATE_TIME): String {
    return this.time.format(pattern)
}
/**
 * 格式化日期对象
 *
 * @param pattern 时间格式模板
 */
fun Date.format(pattern: String = PATTERN_DATE_TIME): String = SimpleDateFormat(pattern, Locale.CHINA).format(this)
/**
 * 格式化时间戳
 *
 * @param pattern 时间格式模板
 * @param handlerUnix 兼容处理Unix系统的时间戳
 */
fun Long?.format(
    pattern: String = PATTERN_DATE_TIME,
    handlerUnix: Boolean = true
): String {
    if (null == this) return ""
    val length = this.toString().length
    val ms = if (handlerUnix && length == 10) this * 1000 else this
    return try {
        Date(ms).format(pattern)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
/**
 * 字符串转日期对象
 *
 * @param pattern 时间格式模板
 */
fun String?.parse(pattern: String = PATTERN_DATE_TIME): Date = try {
    if (this.isNullOrEmpty()) Date(0L) else SimpleDateFormat(pattern, Locale.CHINA).parse(this)
} catch (e: Exception) {
    Date(0L)
}
/**
 * 时间格式转换
 *
 * @param sourcePattern 原来的时间格式模板
 * @param newPattern 新的时间格式模板
 */
fun String?.convert(sourcePattern: String, newPattern: String): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        val date = this.parse(sourcePattern)
        date.format(newPattern)
    } catch (e: Exception) {
        ""
    }
}

/**
 * 字符串转时间戳
 *
 * @param pattern 时间格式模板
 */
fun String?.toTime(pattern: String = PATTERN_DATE_TIME): Long {
    if (this.isNullOrEmpty()) return 0
    return this.parse(pattern).time
}

/** 获取一天的开始时间(00:00:00)  */
fun Calendar.startTimeOfDay(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    return this
}
fun Long.startTimeOfDay(): Long = calendar(this).startTimeOfDay().timeInMillis
/** 获取一天的结束时间(23:59:59)  */
fun Calendar.endTimeOfDay(): Calendar {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    return this
}
fun Long.endTimeOfDay(): Long = calendar(this).endTimeOfDay().timeInMillis
/** 获取当前月份的第一天 */
fun Long.startDayOfMonth(): Long {
    val calendar = calendar(this).startTimeOfDay()
    // 设置月份的日期到该月的第一天
    calendar.set(Calendar.DATE, 1)
    return calendar.timeInMillis
}
/** 获取当前月份的最后一天 */
fun Long.endDayOfMonth(): Long {
    val calendar = calendar(this).endTimeOfDay()
    // 设置月份的日期到该月份的最后一天
    calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    return calendar.timeInMillis
}

/** 日期加减计算，默认加减单位：天  */
fun Long.add(offset: Int, filed: Int = Calendar.DAY_OF_MONTH): Long {
    val calendar = calendar(this)
    calendar.add(filed, offset)
    return calendar.timeInMillis
}
/** 日期加减计算，默认加减单位：天  */
fun Date.add(offset: Int, filed: Int = Calendar.DAY_OF_MONTH): Long {
    val calendar = calendar(this)
    calendar.add(filed, offset)
    return calendar.timeInMillis
}

/** 获取时间，精确到分钟(秒重置为0) */
fun Long.accurateToMinute(): Long {
    val calendar = calendar(this)
    calendar.set(Calendar.SECOND, 0)
    return calendar.timeInMillis
}

/** 将目标时间戳的年月日设置为今天，时分秒保持不变 */
fun Long.copyToToday(): Long {
    val calendar = calendar(this)
    val today = calendar().apply {
        set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        set(Calendar.SECOND, calendar.get(Calendar.SECOND))
    }
    return today.timeInMillis
}

/**
 * 根据日期获得星期
 */
fun Long.getDayOfWeek(): Int {
    val calendar = calendar(this)
    var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    if (dayOfWeek < 0) {
        dayOfWeek = 0
    }
    return dayOfWeek
}

const val SECOND_2_MINUTE = 60
const val SECOND_2_HOUR = SECOND_2_MINUTE * 60
const val SECOND_2_DAY = SECOND_2_HOUR * 24

/**
 * 格式化秒
 * > 示例：01:56:30
 */
fun Int.formatSecond(): String {
    if (this <= 0) return "00:00"
    val hour = if (this >= SECOND_2_HOUR) this / SECOND_2_HOUR else 0
    val minute = if (this >= SECOND_2_MINUTE) ((this - hour * SECOND_2_HOUR) / SECOND_2_MINUTE) else 0
    val second = if (this < SECOND_2_MINUTE) this else (this - hour * SECOND_2_HOUR - minute * SECOND_2_MINUTE)
    return if (hour > 0) {
        "${hour.fillZero()}:${minute.fillZero()}:${second.fillZero()}"
    } else {
        "${minute.fillZero()}:${second.fillZero()}"
    }
}


/**
 * 格式化: 时分秒，如果是个位数，前面补零
 * > 示例：1:6:19 -> 01:06:19
 */
fun Int.fillZero() = if (this > 9) this.toString() else "0$this"


class TimeModel {
    var day: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var second: Int = 0
}

/**
 * 将秒数转换为：x天x小时x分钟x秒
 *
 * @see useDay 是否计算天数。true:计算，false:不计算
 */
fun Int.convert(useDay: Boolean = false): TimeModel {
    val model = TimeModel()
    if (this <= 0) return model
    if (useDay) model.day = this / SECOND_2_DAY
    model.hour = (this - model.day * SECOND_2_DAY) / SECOND_2_HOUR
    model.minute =  (this - model.day * SECOND_2_DAY - model.hour * SECOND_2_HOUR) / SECOND_2_MINUTE
    model.second = this % SECOND_2_MINUTE
    return model
}

/**
 * 将毫秒数转换为：x天x小时x分钟x秒
 *
 * @see useDay 是否计算天数。true:计算，false:不计算
 */
fun Long.convertMs(useDay: Boolean = false): TimeModel {
    val model = TimeModel()
    if (this <= 0) return model
    if (this in 1..999) {
        // 严谨性处理，小于1000毫秒并且大于0毫秒直接的数字，直接返回1秒
        model.second = 1
        return model
    }
    return (this / 1000).toInt().convert(useDay)
}

fun TimeModel.format(hourLabel: String = ":", minuteLabel: String = ":", secondLabel: String = ""): String {
    return hour.fillZero() + hourLabel + minute.fillZero() + minuteLabel + second.fillZero() + secondLabel
}
fun TimeModel.formatFull(): String {
    return "${day.fillZero()}:${hour.fillZero()}:${minute.fillZero()}:${second.fillZero()}"
}

/**
 * 是今天
 *
 * @param handlerUnix 兼容处理Unix系统的时间戳
 */
fun Long.isToday(handlerUnix: Boolean = true): Boolean {
    val ms = if (handlerUnix && this.toString().length == 10) this * 1000 else this
    val target = calendar(ms)
    val current = calendar()
    return target.year() == current.year() && target.month() == current.month() && target.day() == current.day()
}

class DateModel {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    override fun toString(): String {
        return when {
            year == 0 && month == 0 -> "${day}天"
            year == 0 && day == 0 -> "${month}个月"
            month == 0 && day == 0 -> "${year}年"
            year == 0 -> "${month}个月零${day}天"
            month == 0 -> "${year}年零${day}天"
            day == 0 -> "${year}年${month}个月"
            else -> "${year}年${month}个月零${day}天"
        }
    }
}
/**
 * 计算日期差
 *
 * @param startTime 开始时间，单位：毫秒
 * @param endTime 结束时间，单位：毫秒
 * @return 两个日期相差几年几个月零几天
 */
fun calculateDateDiff(startTime: Long, endTime: Long = System.currentTimeMillis()): DateModel {
    return calculateDateDiff(startTime.format(PATTERN_DATE), endTime.format(PATTERN_DATE))
}
/**
 * 计算日期差
 *
 * ```
 * 起始时间：2016-01-19，结束时间：2023-04-14，相差：7年2个月零26天
 * ```
 *
 * @param startTime 开始时间，格式：2016-01-19
 * @param endTime 结束时间，格式：2016-01-19
 * @return 两个日期相差几年几个月零几天
 */
fun calculateDateDiff(startTime: String, endTime: String): DateModel {
    return calculateDateDiff(LocalDate.parse(startTime), LocalDate.parse(endTime))
}
/**
 * 计算日期差
 *
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @return 两个日期相差几年几个月零几天
 */
fun calculateDateDiff(startTime: LocalDate, endTime: LocalDate): DateModel {
    val diff = DateModel()
    Period.between(startTime, endTime).apply {
        diff.year = years
        diff.month = months
        diff.day = days
    }
    // Timber.tag("Date").e("起始时间：${start}，结束时间：${end}，相差：${diff}")
    return diff
}

/**
 * 计算日期差几天
 *
 * @param startTime 开始时间，格式：2016-01-19
 * @param endTime 结束时间，格式：2016-01-19
 */
fun calculateDayDiff(startTime: String, endTime: String): Long {
    return calculateDayDiff(LocalDate.parse(startTime), LocalDate.parse(endTime))
}
/**
 * 计算日期差几天
 *
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @return 两个日期相差几天
 */
fun calculateDayDiff(startTime: LocalDate, endTime: LocalDate): Long {
    @Suppress("UnnecessaryVariable") val day = startTime.toEpochDay() - endTime.toEpochDay()
    // Timber.tag("Date").e("起始时间：${startTime}，结束时间：${endTime}，相差：${day} 天")
    return day
}