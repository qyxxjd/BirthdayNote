@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.classic.core.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * Base Fragment
 *
 * @author Classic
 * @date 2020-01-07 11:13
 */
abstract class CoreFragment : Fragment() {
    lateinit var appContext: Context
    private val isHiddenState = "isHiddenState"
    private var weakContext: WeakReference<AppCompatActivity>? = null

    /**
     * 使用弱引用的Activity进行操作
     */
    fun action(action: (AppCompatActivity) -> Unit) {
        weakContext?.get()?.let {
            if (isAdded) action.invoke(it)
        }
    }

    /**
     * 获取参数
     */
    open fun onObtainParams() {}

    /**
     * 类名
     */
    fun label(): String = javaClass.simpleName

    /**
     * FragmentManager
     */
    open fun supportFm() = requireActivity().supportFragmentManager
    /**
     * FragmentManager
     */
    open fun fm() = childFragmentManager
    /**
     * FragmentManager
     */
    open fun parentFm() = parentFragmentManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag(label())
        appContext = context.applicationContext
        weakContext = WeakReference(context as AppCompatActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onObtainParams()
    }

    /**
     * 移除DialogFragment
     */
    fun remove(manager: FragmentManager, allowStateLoss: Boolean = true) {
        try {
            val fragment = this
            val newTag = label()
            if (isAdded || null != manager.findFragmentByTag(newTag)) {
                manager.commitNow(allowStateLoss) { remove(fragment) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 基于Deferred的扩展函数创建一个启动协程，该协程将调用await()并将返回的值传递给block()。
     */
    infix fun <T> Deferred<T>.ui(block: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (isActive) block(this@ui.await())
        }
    }
}
