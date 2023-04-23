package com.classic.birthday.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.classic.birthday.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Dialog util
 *
 * @author Classic
 * @version 2023/4/23 9:37
 */
object DialogUtil {

    fun create(context: Context, titleResId: Int, contentResId: Int, cancelable: Boolean = false): AlertDialog {
        return MaterialAlertDialogBuilder(context, R.style.AlertDialog_Theme)
            .setCancelable(cancelable)
            .setTitle(titleResId)
            .setMessage(contentResId)
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun confirm(context: Context, title: String, contentResId: Int, cancelable: Boolean = false,
                listener: DialogInterface.OnClickListener? = null): AlertDialog {
        return MaterialAlertDialogBuilder(context, R.style.AlertDialog_Theme)
            .setCancelable(cancelable)
            .setTitle(title)
            .setMessage(contentResId)
            // .setNegativeButton("取消") { dialog, which ->
            //     dialog.dismiss()
            //     listener?.onClick(dialog, which)
            // }
            .setPositiveButton("确定") { dialog, which ->
                dialog.dismiss()
                listener?.onClick(dialog, which)
            }
            .show()
    }

    fun custom(context: Context, title: String, content: String,
               negativeMenu: String, positiveMenu: String,
               cancelable: Boolean = false,
               listener: DialogInterface.OnClickListener? = null): AlertDialog {
        return MaterialAlertDialogBuilder(context, R.style.AlertDialog_Theme)
            .setCancelable(cancelable)
            .setTitle(title)
            .setMessage(content)
            .setNegativeButton(negativeMenu) { dialog, which ->
                dialog.dismiss()
                listener?.onClick(dialog, which)
            }
            .setPositiveButton(positiveMenu) { dialog, which ->
                dialog.dismiss()
                listener?.onClick(dialog, which)
            }
            .show()
    }
}