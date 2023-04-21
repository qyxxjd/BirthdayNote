package com.classic.core.interfaces

import java.io.File

/**
 * 文件保存监听
 *
 * @author Classic
 * @date 4/29/21 17:18 PM
 */
interface FileSaveListener {

    /**
     * 保存成功回调
     */
    fun onSuccess(file: File)

    /**
     * 保存失败回调
     */
    fun onFailure(message: String?)
}