@file:Suppress("unused")

package com.classic.core.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel
 *
 * @author Classic
 * @date 2020/6/11 4:10 PM
 */
open class CoreViewModel : ViewModel() {
    protected val httpContext = viewModelScope.coroutineContext
    protected val pageTotalSize = 20
    protected val tasks = mutableListOf<Deferred<*>?>()

    fun cancel(deferred: Deferred<*>?) {
        if (null != deferred && !deferred.isCancelled) {
            deferred.cancel()
        }
    }

    /**
     * 类名
     */
    fun label(): String = javaClass.simpleName

    protected fun <T> execute(block: suspend () -> T?): LiveData<T?> {
        return flow { emit(block()) }
            .flowOn(Dispatchers.IO)
            .catch {
                it.printStackTrace()
                emit(null)
            }
            .asLiveData(httpContext)
    }

    override fun onCleared() {
        super.onCleared()
        unsubscribe()
        for (item in tasks) {
            cancel(item)
        }
    }

    /**
     * 订阅逻辑
     */
    open fun subscribe(context: Context) {
        Timber.tag(label())
    }

    /**
     * 取消订阅逻辑
     */
    open fun unsubscribe() {}

    /**
     * 基于Deferred的扩展函数创建一个启动协程，该协程将调用await()并将返回的值传递给block()。
     */
    infix fun <T> Deferred<T>.ui(block: suspend (T) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            if (isActive) block(this@ui.await())
        }
    }
}