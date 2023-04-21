@file:Suppress("unused")

package com.classic.core.ui

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

enum class LiveDataState {
    Idle, Loading, Success, Error, Complete
}

/**
 * 带状态标示的LiveData
 *
 * @author Classic
 * @date 2020/6/11 7:49 PM
 */
class StateLiveData<T> : AppLiveData<T>() {
    val state = MutableLiveData<LiveDataState>()
    fun postState(s: LiveDataState) {
        state.postValue(s)
    }
}

/**
 * 只有成功或失败的LiveData
 */
class SingleLiveData<T> : AppLiveData<T>() {
    val state = MutableLiveData<AppThrowable>()
    fun postState(e: AppThrowable) {
        state.postValue(e)
    }
}

open class AppLiveData<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)
    @Suppress("MoveLambdaOutsideParentheses")
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }
    override fun postValue(value: T) {
        mPending.set(true)
        super.postValue(value)
    }
}

open class AppThrowable(
    val code: String?,
    override val message: String?,
    val throwable: Throwable? = null
): Throwable(message, throwable)