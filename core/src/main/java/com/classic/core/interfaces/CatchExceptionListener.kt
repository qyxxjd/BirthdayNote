package com.classic.core.interfaces

/**
 * 捕获异常接口
 *
 * @author Classic
 * @date 2021/9/29 14:41
 */
interface CatchExceptionListener {

    /**
     * 捕获到异常时回调
     */
    fun onCatchException(e: Exception)
}