@file:Suppress("unused")

package com.classic.core.ext

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * Fragment extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
inline operator fun <reified T> Bundle?.get(key: String): T? =
    this?.get(key).let { if (it is T) it else null }
inline operator fun <reified T> Fragment?.get(key: String): T? =
    this?.arguments?.get(key).let { if (it is T) it else null }

/**
 * 添加参数
 *
 * ```
 * // Sample
 * XxxFragment().apply {
 *     addArgument(KEY_STATUS to status, KEY_XXX to xxx, ...)
 * }
 * ```
 */
fun Fragment.addArgument(vararg pairs: Pair<String, Any?>) {
    this.arguments = bundleOf(*pairs)
}
fun Fragment.addStringArgument(key: String, value: String) {
    val bundle = Bundle()
    bundle.putString(key, value)
    this.arguments = bundle
}
fun Fragment.addIntArgument(key: String, value: Int) {
    val bundle = Bundle()
    bundle.putInt(key, value)
    this.arguments = bundle
}
fun Fragment.addLongArgument(key: String, value: Long) {
    val bundle = Bundle()
    bundle.putLong(key, value)
    this.arguments = bundle
}
fun Fragment.addDoubleArgument(key: String, value: Double) {
    val bundle = Bundle()
    bundle.putDouble(key, value)
    this.arguments = bundle
}
fun Fragment.addBooleanArgument(key: String, value: Boolean) {
    val bundle = Bundle()
    bundle.putBoolean(key, value)
    this.arguments = bundle
}
fun Fragment.addSerializableArgument(key: String, value: Serializable) {
    val bundle = Bundle()
    bundle.putSerializable(key, value)
    this.arguments = bundle
}
fun Fragment.addParcelableArgument(key: String, value: Parcelable) {
    val bundle = Bundle()
    bundle.putParcelable(key, value)
    this.arguments = bundle
}

/** 获取参数 */
fun Fragment.getStringArgument(key: String): String {
    return this.arguments?.getString(key) ?: ""
}
fun Fragment.getIntArgument(key: String, defaultValue: Int = 0): Int {
    return this.arguments?.getInt(key, defaultValue) ?: defaultValue
}
fun Fragment.getLongArgument(key: String, defaultValue: Long = 0): Long {
    return this.arguments?.getLong(key, defaultValue) ?: defaultValue
}
fun Fragment.getDoubleArgument(key: String, defaultValue: Double = 0.0): Double {
    return this.arguments?.getDouble(key, defaultValue) ?: defaultValue
}
fun Fragment.getBooleanArgument(key: String, defaultValue: Boolean = false): Boolean {
    return this.arguments?.getBoolean(key, defaultValue) ?: defaultValue
}
fun Fragment.getSerializableArgument(key: String): Serializable? {
    return this.arguments?.getSerializable(key)
}
fun <T> Fragment.serializable(key: String): T? {
    this.arguments?.getSerializable(key)?.let {
        @Suppress("UNCHECKED_CAST")
        return (it as T)
    }
    return null
}
fun <T : Parcelable> Fragment.parcelable(key: String): T? {
    this.arguments?.getParcelable<Parcelable>(key)?.let {
        @Suppress("UNCHECKED_CAST")
        return (it as T)
    }
    return null
}