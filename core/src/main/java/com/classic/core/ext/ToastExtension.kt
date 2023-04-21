@file:Suppress("unused")

package com.classic.core.ext

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Toast extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */

fun showToast(context: Context, resId: Int) {
    Toast.makeText(context.applicationContext, resId, Toast.LENGTH_SHORT).show()
}

fun showToast(context: Context, content: String) {
    Toast.makeText(context.applicationContext, content, Toast.LENGTH_SHORT).show()
}

fun showLongToast(context: Context, resId: Int) {
    Toast.makeText(context.applicationContext, resId, Toast.LENGTH_LONG).show()
}

fun showLongToast(context: Context, content: String) {
    Toast.makeText(context.applicationContext, content, Toast.LENGTH_LONG).show()
}

fun Context.toast(resId: Int) {
    showToast(applicationContext, resId)
}

fun Context.toast(content: String) {
    showToast(applicationContext, content)
}

fun Context.longToast(resId: Int) {
    showLongToast(applicationContext, resId)
}

fun Context.longToast(content: String) {
    showLongToast(applicationContext, content)
}
fun Context.toastEmptyData(content: String = "没有更多数据了") {
    showToast(applicationContext, content)
}

fun Context.customToast(content: String, time: Long = 5000) {
    val toast = Toast.makeText(applicationContext, content, Toast.LENGTH_LONG)
    toast.show()
    globalUITask(time) { toast.cancel() }
}

fun Fragment.toast(resId: Int) {
    if (isAdded) requireContext().applicationContext.toast(resId)
}
fun Fragment.toast(content: String) {
    if (isAdded) requireContext().applicationContext.toast(content)
}
fun Fragment.longToast(resId: Int) {
    if (isAdded) requireContext().applicationContext.longToast(resId)
}
fun Fragment.longToast(content: String) {
    if (isAdded) requireContext().applicationContext.longToast(content)
}

fun Activity.toast(resId: Int) {
    if (!isDestroyed) applicationContext.toast(resId)
}
fun Activity.toast(content: String) {
    if (!isDestroyed) applicationContext.toast(content)
}
fun Activity.longToast(resId: Int) {
    if (!isDestroyed) applicationContext.longToast(resId)
}
fun Activity.longToast(content: String) {
    if (!isDestroyed) applicationContext.longToast(content)
}

private const val MAX_SNACK_BAR_TEXT_LINES = 5
fun showSnackBar(view: View, msg: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(view, msg, duration)
        .apply {
            this.view
                .findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                .maxLines = MAX_SNACK_BAR_TEXT_LINES
        }
        .show()
}
fun Activity.showSnackBar(@StringRes msgResId: Int) {
    showSnackBar(string(msgResId))
}
fun Activity.showSnackBar(msg: String, duration: Int = Snackbar.LENGTH_SHORT) {
    if (isDestroy()) return
    runOnUiThread {
        showSnackBar(window.decorView, msg, duration)
    }
}
fun Activity.showSnackBarLong(@StringRes msgResId: Int) {
    showSnackBar(string(msgResId), Snackbar.LENGTH_LONG)
}
fun Activity.showSnackBarLong(msg: String) {
    showSnackBar(msg, Snackbar.LENGTH_LONG)
}
fun Fragment.showSnackBar(@StringRes msgResId: Int) {
    showSnackBar(requireContext().string(msgResId))
}
fun Fragment.showSnackBar(msg: String, duration: Int = Snackbar.LENGTH_SHORT) {
    val act = activity
    if (null == act || act.isDestroy()) return
    act.showSnackBar(msg, duration)
}

fun Fragment.showSnackBarLong(@StringRes msgResId: Int) {
    showSnackBar(requireContext().string(msgResId), Snackbar.LENGTH_LONG)
}
fun Fragment.showSnackBarLong(msg: String) {
    showSnackBar(msg, Snackbar.LENGTH_LONG)
}