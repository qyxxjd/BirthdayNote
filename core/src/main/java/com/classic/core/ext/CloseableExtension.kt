@file:Suppress("unused")

package com.classic.core.ext

import java.io.Closeable
import java.io.IOException

/**
 * Closeable extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
fun Closeable?.safeClose() {
    if (null != this) {
        try {
            close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}