package com.classic.core.interfaces

import android.content.DialogInterface

/**
 * 弹窗监听事件
 *
 * @author Classic
 * @date 4/29/21 17:18 PM
 */
interface DialogDismissListener {
    /**
     * 弹窗关闭回调
     */
    fun onDialogDismiss()
}

interface DialogPositiveListener {
    /**
     * 确定菜单点击事件
     */
    fun onPositiveMenuClick(dialog: DialogInterface)
}

interface DialogInterfaceListener : DialogPositiveListener {
    /**
     * 取消菜单点击事件
     */
    fun onNegativeMenuClick(dialog: DialogInterface)
}