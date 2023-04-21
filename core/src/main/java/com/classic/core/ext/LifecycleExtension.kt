@file:Suppress("unused")

package com.classic.core.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Lifecycle extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
fun <T : Any, L : LiveData<T>> AppCompatActivity.observe(liveData: L, body: (T) -> Unit) =
    liveData.observe(this, Observer(body))
fun <T : Any, L : LiveData<T>> Fragment.observe(liveData: L, body: (T) -> Unit) =
    liveData.observe(viewLifecycleOwner, Observer(body))

inline fun Fragment.doOnViewLifecycle(
    crossinline onCreateView: () -> Unit = {},
    crossinline onStart: () -> Unit = {},
    crossinline onResume: () -> Unit = {},
    crossinline onPause: () -> Unit = {},
    crossinline onStop: () -> Unit = {},
    crossinline onDestroyView: () -> Unit = {},
) =
    viewLifecycleOwner.doOnLifecycle(onCreateView, onStart, onResume, onPause, onStop, onDestroyView)

inline fun LifecycleOwner.doOnLifecycle(
    crossinline onCreate: () -> Unit = {},
    crossinline onStart: () -> Unit = {},
    crossinline onResume: () -> Unit = {},
    crossinline onPause: () -> Unit = {},
    crossinline onStop: () -> Unit = {},
    crossinline onDestroy: () -> Unit = {},
) =
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        fun onCreate() = onCreate()
        fun onStart() = onStart()
        fun onResume() = onResume()
        fun onPause() = onPause()
        fun onStop() = onStop()
        fun onDestroy() = onDestroy()
    })