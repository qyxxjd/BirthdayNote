@file:Suppress("unused")

package com.classic.core.ext

import android.database.Cursor
import androidx.core.database.*

/**
 * Cursor extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
fun Cursor.int(columnName: String): Int = getIntOrNull(getColumnIndex(columnName)) ?: 0
fun Cursor.short(columnName: String): Short = getShortOrNull(getColumnIndex(columnName)) ?: 0
fun Cursor.long(columnName: String): Long = getLongOrNull(getColumnIndex(columnName)) ?: 0L
fun Cursor.float(columnName: String): Float = getFloatOrNull(getColumnIndex(columnName)) ?: 0F
fun Cursor.double(columnName: String): Double = getDoubleOrNull(getColumnIndex(columnName)) ?: 0.0
fun Cursor.string(columnName: String): String = getStringOrNull(getColumnIndex(columnName)) ?: ""
fun Cursor.blob(columnName: String): ByteArray = getBlobOrNull(getColumnIndex(columnName)) ?: ByteArray(0)