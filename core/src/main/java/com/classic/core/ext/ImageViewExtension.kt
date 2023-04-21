package com.classic.core.ext

import android.widget.ImageView

/**
 * ImageView
 *
 * @author LiuBin
 * @date 2021/9/18 16:07
 */
fun ImageView?.clear() {
    this?.setImageDrawable(null)
}
fun ImageView?.clearTag() {
    this?.tag = null
}
fun ImageView?.clearImageAndTag() {
    this?.setImageDrawable(null)
    this?.tag = null
}