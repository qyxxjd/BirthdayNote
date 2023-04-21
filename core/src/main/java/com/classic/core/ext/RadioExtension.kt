@file:Suppress("unused")

package com.classic.core.ext

import android.widget.RadioButton

/**
 * RadioButton extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */

/**
 * 选中
 */
fun RadioButton?.choose() {
    this?.isChecked = true
}