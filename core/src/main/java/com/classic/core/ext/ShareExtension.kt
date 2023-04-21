@file:Suppress("unused", "SpellCheckingInspection")

package com.classic.core.ext

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.classic.core.data.MIME

/**
 * Share extensions
 *
 * @author Classic
 * @date 2019-12-31 15:48
 */
fun Activity.share(
    contentType: String,
    shareFileUri: Uri? = null,
    contentText: String = "",
    title: String = "",
    requestCode: Int = -1,
    componentPackageName: String = "",
    componentClassName: String = ""
) {
    if (contentType.isEmpty()) return
    if (MIME.TEXT == contentType && contentText.isEmpty()) return

    var shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = contentType
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    shareIntent.addCategory(Intent.CATEGORY_DEFAULT)
    if (componentPackageName.isNotEmpty() && componentClassName.isNotEmpty()) {
        shareIntent.component = ComponentName(componentPackageName, componentClassName)
    }
    when (contentType) {
        MIME.TEXT -> {
            shareIntent.putExtra(Intent.EXTRA_TEXT, contentText)
        }
        MIME.IMAGE, MIME.AUDIO, MIME.VIDEO, MIME.EXCEL_XLS, MIME.FILE -> {
            shareIntent.putExtra(Intent.EXTRA_STREAM, shareFileUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        else -> {
            return
        }
    }
    shareIntent = Intent.createChooser(shareIntent, title)
    if (shareIntent.resolveActivity(packageManager) != null) {
        try {
            if (requestCode != -1) {
                startActivityForResult(shareIntent, requestCode)
            } else {
                startActivity(shareIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        Toast.makeText(applicationContext, "不支持的文件类型", Toast.LENGTH_SHORT).show()
    }
}