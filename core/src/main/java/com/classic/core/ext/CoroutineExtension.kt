@file:Suppress("unused", "SpellCheckingInspection")

package com.classic.core.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/**
 * 协程扩展
 *
 * > 避免使用 `GlobalScope` ，使用 `applicationScope` 进行替代
 *
 * @author Classic
 * @date 2019-04-24 10:35
 */
/** 执行计算密集型任务 */
fun AppCompatActivity.task(delayTimeMillis: Long = 0, dispatcher: CoroutineDispatcher = Dispatchers.Default, task: suspend () -> Unit) {
    lifecycleScope.launch(dispatcher) {
        if (delayTimeMillis > 0) delay(delayTimeMillis)
        if (isActive) task()
    }
}
/** 执行IO耗时任务 */
fun AppCompatActivity.ioTask(delayTimeMillis: Long = 0, task: suspend () -> Unit) {
    task(delayTimeMillis, Dispatchers.IO, task)
}
/** 执行UI任务 */
fun AppCompatActivity.uiTask(delayTimeMillis: Long = 0, task: suspend () -> Unit) {
    task(delayTimeMillis, Dispatchers.Main, task)
}

fun Fragment.task(delayTimeMillis: Long = 0, dispatcher: CoroutineDispatcher = Dispatchers.Default, task: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch(dispatcher) {
        if (delayTimeMillis > 0) delay(delayTimeMillis)
        if (isActive) task()
    }
}
fun Fragment.ioTask(delayTimeMillis: Long = 0, task: suspend () -> Unit) {
    task(delayTimeMillis, Dispatchers.IO, task)
}
fun Fragment.uiTask(delayTimeMillis: Long = 0, task: suspend () -> Unit) {
    task(delayTimeMillis, Dispatchers.Main, task)
}

fun ViewModel.task(delayTimeMillis: Long = 0, dispatcher: CoroutineDispatcher = Dispatchers.Default, task: suspend () -> Unit) {
    viewModelScope.launch(dispatcher) {
        if (delayTimeMillis > 0) delay(delayTimeMillis)
        if (isActive) task()
    }
}
fun ViewModel.ioTask(delayTimeMillis: Long = 0, task: suspend () -> Unit) {
    task(delayTimeMillis, Dispatchers.IO, task)
}
fun ViewModel.uiTask(delayTimeMillis: Long = 0, task: suspend () -> Unit) {
    task(delayTimeMillis, Dispatchers.Main, task)
}

// 不需要取消这个作用域，因为它会被进程拆除
val applicationScopeUI = CoroutineScope(SupervisorJob() + Dispatchers.Main)
val applicationScopeIO = CoroutineScope(SupervisorJob() + Dispatchers.IO)
val applicationScopeDefault = CoroutineScope(SupervisorJob() + Dispatchers.Default)
/**
 * 执行一个全局的计算密集型任务
 *
 * > `Activity、Fragment、ViewModel`慎用，随着进程自动销毁
 *
 * @param delayTimeMillis 可选参数：延迟一定时间后执行任务
 */
fun <T> globalTask(delayTimeMillis: Long = 0, task: suspend () -> T) {
    applicationScopeDefault.launch {
        if (delayTimeMillis > 0) delay(delayTimeMillis)
        if (isActive) task()
    }
}
/**
 * 执行一个全局的IO任务
 *
 * > `Activity、Fragment、ViewModel`慎用，随着进程自动销毁
 *
 * @param delayTimeMillis 可选参数：延迟一定时间后执行任务
 */
fun <T> globalIOTask(delayTimeMillis: Long = 0, task: suspend () -> T) {
    applicationScopeIO.launch {
        if (delayTimeMillis > 0) delay(delayTimeMillis)
        if (isActive) task()
    }
}
/**
 * 执行一个全局的UI任务
 *
 * > `Activity、Fragment、ViewModel`慎用，随着进程自动销毁
 *
 * @param delayTimeMillis 可选参数：延迟一定时间后执行任务
 */
fun <T> globalUITask(delayTimeMillis: Long = 0, task: suspend () -> T) {
    applicationScopeUI.launch {
        if (delayTimeMillis > 0) delay(delayTimeMillis)
        if (isActive) task()
    }
}

suspend fun <T> with(delayTimeMillis: Long = 0, dispatcher: CoroutineDispatcher = Dispatchers.Default, task: suspend () -> T) {
    withContext(dispatcher) {
        if (delayTimeMillis > 0) delay(delayTimeMillis)
        if (isActive) task()
    }
}
/** 协程内部IO操作 */
suspend fun <T> withIO(delayTimeMillis: Long = 0, task: suspend () -> T) {
    with(delayTimeMillis, Dispatchers.IO, task)
}
/** 协程内部UI操作 */
suspend fun <T> withUI(delayTimeMillis: Long = 0, task: suspend () -> T) {
    with(delayTimeMillis, Dispatchers.Main, task)
}

/**
 * 执行一个异步任务
 *
 * > 任务与 `Activity、Fragment、ViewModel` 声明周期联动
 * > 进行 `IO` 和网络操作时，使用 `Dispatchers.IO` 更高效。
 *
 * @param dispatcher 可选参数：协程调度
 * @return Deferred
 */
fun <T> LifecycleOwner.async(dispatcher: CoroutineDispatcher = Dispatchers.IO, task: suspend () -> T): Deferred<T> {
    return lifecycleScope.async(dispatcher) {
        task()
    }
}
fun <T> ViewModel.async(dispatcher: CoroutineDispatcher = Dispatchers.IO, task: suspend () -> T): Deferred<T> {
    return viewModelScope.async(dispatcher) {
        task()
    }
}