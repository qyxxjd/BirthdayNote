package com.classic.core.interfaces

/**
 * 通用回调。返回单个值，并且结果不能为空
 */
interface SingleCallback<T> {
    fun onSingleResult(t: T)
}

/**
 * 通用回调。返回单个值，结果可能为空
 */
interface MaybeCallback<T> {
    fun onMaybeResult(t: T?)
}