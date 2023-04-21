@file:Suppress("unused")

package com.classic.core.ext

/**
 * Boolean extensions
 *
 * @author Classic
 * @date 2021-03-03 15:48
 */

/** Int转Boolean */
fun Int?.toBoolean(): Boolean = (null != this && this == 1)

/** Boolean转Int */
fun Boolean?.toInt(): Int = if (null != this && this) 1 else 0

/** Boolean转开关描述 */
fun Boolean?.toSwitch(on: String = "开启", off: String = "关闭"): String = if (true == this) on else off