@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.classic.core.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.classic.core.ext.hideKeyboard
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Base activity
 *
 * @author Classic
 * @date 2020-01-07 11:13
 */
abstract class CoreActivity : AppCompatActivity() {
    lateinit var appContext: Context

    /**
     * 获取参数
     */
    open fun onObtainParams() {}

    /**
     * 自动隐藏输入法
     */
    open fun autoHideKeyboard(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(label())
        appContext = applicationContext
        onObtainParams()
    }

    override fun finish() {
        super.finish()
        if (autoHideKeyboard()) hideKeyboard()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            // 设置为当前的 Intent，避免 Activity 被杀死后重启 Intent 还是最原先的那个
            setIntent(it)
        }
        onObtainParams()
    }

    /**
     * 和 setContentView 对应的方法
     */
    open fun getContentView(): ViewGroup? {
        return findViewById(Window.ID_ANDROID_CONTENT)
    }

    /**
     * 子类需要重写该方法，传入EditText
     */
    open fun hideKeyboard() {
        hideKeyboard(currentFocus)
    }

    /**
     * 类名
     */
    fun label(): String = javaClass.simpleName

    /**
     * FragmentManager
     */
    fun fm() = supportFragmentManager
    /**
     * 添加Fragment
     *
     * > 生命周期不会回调，推荐使用replaceFragment
     *
     * @param container 容器视图ID
     * @param fragment 要添加的Fragment
     */
    fun addFragment(container: Int, fragment: Fragment?) {
        fragment?.let {
            val newTag = it::class.java.simpleName
            if (!it.isAdded && null == fm().findFragmentByTag(newTag)) {
                fm().commitNow(allowStateLoss = true) { add(container, it, newTag) }
            }
        }
    }

    /**
     * 替换Fragment
     *
     * > 生命周期可以正常回调
     *
     * @param container 容器视图ID
     * @param fragment 要添加的Fragment
     */
    fun replaceFragment(container: Int, fragment: Fragment?, removeOldFragment: Boolean = false) {
        fragment?.let {
            val newTag = it::class.java.simpleName
            if (removeOldFragment) removeFragment(fragment)
            if (!it.isAdded && null == fm().findFragmentByTag(newTag)) {
                fm().commitNow(allowStateLoss = true) { replace(container, it, newTag) }
            }
        }
    }
    /**
     * 显示Fragment
     *
     * @param fragment 要显示的Fragment
     */
    fun showFragment(fragment: Fragment?) {
        fragment?.let {
            if (it.isHidden) fm().commitNow(allowStateLoss = true) { show(it) }
        }
    }
    /**
     * 隐藏Fragment
     *
     * @param fragment 要隐藏的Fragment
     */
    fun hideFragment(fragment: Fragment?) {
        fragment?.let {
            if (it.isVisible) fm().commitNow(allowStateLoss = true) { hide(it) }
        }
    }
    /**
     * 移除Fragment
     *
     * @param fragment 要移除的Fragment
     */
    fun removeFragment(fragment: Fragment?) {
        fragment?.let {
            fm().commitNow(allowStateLoss = true) { remove(it) }
        }
    }
    /**
     * 恢复Fragment
     *
     * @param tag Fragment标示
     */
    fun <T> restoreFragment(tag: String): T? {
        return fm().findFragmentByTag(tag)?.let {
            @Suppress("UNCHECKED_CAST")
            it as T
        }
    }

    /**
     * 基于Deferred的扩展函数创建一个启动协程，该协程将调用await()并将返回的值传递给block()。
     */
    infix fun <T> Deferred<T>.ui(block: suspend (T) -> Unit) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (isActive) block(this@ui.await())
        }
    }
}