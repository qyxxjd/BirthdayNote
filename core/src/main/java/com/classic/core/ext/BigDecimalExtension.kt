@file:Suppress("unused")

package com.classic.core.ext

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * BigDecimal extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */

/** 默认精确到小数点后2位 */
const val DEFAULT_SCALE: Int = 2

/**
 * 四舍五入
 *
 * @param scale 精确到小数点后几位
 * @param mode 舍入的模式
 * @see RoundingMode
 */
fun String.round(scale: Int = DEFAULT_SCALE, mode: RoundingMode = RoundingMode.HALF_UP): String =
    BigDecimal(this).setScale(scale, mode).toString()
fun Float.round(scale: Int = DEFAULT_SCALE, mode: RoundingMode = RoundingMode.HALF_UP): Float =
    BigDecimal(this.toString()).setScale(scale, mode).toFloat()
fun Double.round(scale: Int = DEFAULT_SCALE, mode: RoundingMode = RoundingMode.HALF_UP): Double =
    BigDecimal(this.toString()).setScale(scale, mode).toDouble()

/**
 * 四舍五入，并去掉小数点后无效的0
 *
 * @param scale 精确到小数点后几位
 * @param mode 舍入的模式
 * @see RoundingMode
 */
fun String?.replace(scale: Int = 0, mode: RoundingMode = RoundingMode.HALF_UP): String {
    if (this.isNullOrEmpty()) return "0"
    return try {
        var result = if (scale > 0) round(scale, mode) else this
        if (result.indexOf(".") > 0) {
            result = result.replace("0+?$".toRegex(), "") // 去掉后面无用的零
            result = result.replace("[.]$".toRegex(), "") // 如小数点后面全是零则去掉小数点
        }
        result
    } catch (e: NumberFormatException) {
        "0"
    }
}

/**
 * 四舍五入，并去掉小数点后无效的0
 *
 * @param scale 精确到小数点后几位
 * @param mode 舍入的模式
 * @see RoundingMode
 *
 * #### 常用的模式：
 * - BigDecimal.ROUND_HALF_UP >=0.5，向前一位舍入
 * - BigDecimal.ROUND_DOWN 始终截断，不舍入
 */
fun Number?.replace(scale: Int = 0, mode: RoundingMode = RoundingMode.HALF_UP): String =
    this?.toString()?.replace(scale, mode) ?: "0"

/**
 * 通用金额显示
 */
fun Number?.toPrice(): String = "¥${this.toText()}"
/**
 * 通用负金额显示
 */
fun Number?.toNegativePrice(): String = "-¥${this.toText()}"

/**
 * 数字转字符串，精确到小数点后2位，去掉小数点后无效的0
 */
fun Number?.toText(): String = this.replace(DEFAULT_SCALE)

/**
 * 排除负金额显示，最小显示0
 *
 * > 支付场景，使用超过支付金额的优惠券时，会出现负数，需要处理
 */
fun Double.avoidNegativePrice(): String {
    return if (this < 0.0) "¥0" else this.toPrice()
}

/**
 * 格式化数字
 *
 * > 默认格式
 * > 1666888999 -> "1,666,888,999"
 */
fun Long?.formatDecimal(pattern: String = "###,##0"): String {
    if (null == this) return ""
    return DecimalFormat(pattern).format(this)
}

/**
 * 以万为单位，格式化数字
 *
 * @param symbol 万的描述字符，默认：w
 * @param suffix 后缀字符串，可选项
 * @param scale 数据大于万时，精确到小数点后几位
 */
fun Int?.convertW(symbol: String = "w", suffix: String = "", scale: Int = 2): String {
    if (null == this) return "0${suffix}"
    return when {
        this > 10000 -> "${(this / 10000.0).replace(scale)}${symbol}$suffix"
        else -> "${this}${suffix}"
    }
}
fun Long?.convertW(symbol: String = "w", suffix: String = "", scale: Int = 2): String {
    if (null == this) return "0${suffix}"
    return when {
        this > 10000 -> "${(this / 10000.0).replace(scale)}${symbol}$suffix"
        else -> "${this}${suffix}"
    }
}
