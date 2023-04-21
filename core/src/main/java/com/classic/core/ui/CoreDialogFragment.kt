@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.classic.core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.classic.core.ext.hideKeyboard
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * Dialog fragment
 *
 * @author Classic
 * @date 2020-02-01 08:02
 */
abstract class CoreDialogFragment : AppCompatDialogFragment() {
    protected lateinit var appContext: Context
    private var weakContext: WeakReference<AppCompatActivity>? = null
    private var canCancelable = true

    /**
     * 弹窗外部背景透明，默认不透明
     */
    open var useBackgroundTransparent = false

    /**
     * 获取参数
     */
    open fun onObtainParams() {}

    /**
     * 自动隐藏输入法
     */
    open fun autoHideKeyboard(): Boolean = false

    /**
     * 类名
     */
    fun label(): String = javaClass.simpleName

    /**
     * FragmentManager
     */
    fun fm() = childFragmentManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag(label())
        appContext = context.applicationContext
        weakContext = WeakReference(context as AppCompatActivity)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onObtainParams()

        if (autoHideKeyboard()) {
            // 隐藏输入法：用户点击空白区域关闭弹窗时
            dialog?.let {
                it.window?.decorView?.setOnTouchListener { _, motionEvent ->
                    val token = it.currentFocus?.windowToken
                    if (MotionEvent.ACTION_DOWN == motionEvent.action && null != token) {
                        hideKeyboard()
                    }
                    false
                }
            }
        }
    }

    // 隐藏输入法：手动关闭弹窗时
    override fun dismiss() {
        if (autoHideKeyboard()) hideKeyboard()
        super.dismiss()
    }

    /**
     * 子类需要重写该方法，传入EditText
     */
    open fun hideKeyboard() {
        requireActivity().hideKeyboard(requireActivity().currentFocus)
    }

    override fun onStart() {
        super.onStart()
        isCancelable = canCancelable
        dialog?.apply {
            setCancelable(canCancelable)
            setCanceledOnTouchOutside(canCancelable)
            if (!canCancelable) {
                setOnKeyListener { _, keyCode, keyEvent ->
                    // 拦截返回键，点击无反应
                    keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN
                }
            }
        }
        dialog?.window?.apply {
            val params: WindowManager.LayoutParams = attributes
            customWindow(params)
            attributes = params
            // 设置弹窗内部的背景颜色
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 部分机型弹出DialogFragment会有一圈边距，这里做一下兼容。
            decorView.setPadding(0, 0, 0, 0)
            isCancelable = canCancelable
        }
    }

    /**
     * 使用弱引用的Activity进行操作
     */
    fun action(action: (AppCompatActivity) -> Unit) {
        weakContext?.get()?.let {
            if (isAdded) action.invoke(it)
        }
    }

    /**
     * 显示DialogFragment
     */
    fun show(manager: FragmentManager, allowStateLoss: Boolean = true) {
        try {
            val fragment = this
            val newTag = label()
            if (isAdded || null != manager.findFragmentByTag(newTag)) {
                // 已添加时，直接显示
                manager.commitNow(allowStateLoss) { show(fragment) }
            } else {
                // 未添加过
                manager.commitNow(allowStateLoss) { add(fragment, newTag) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 移除DialogFragment
     */
    fun remove(manager: FragmentManager, allowStateLoss: Boolean = true) {
        if (!isAdded) return
        try {
            val fragment = this
            manager.commitNow(allowStateLoss) { remove(fragment) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 自定义Dialog样式
     */
    open fun customWindow(params: WindowManager.LayoutParams) {
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.CENTER
        if (useBackgroundTransparent) {
            // 设置弹窗外部的背景透明度
            params.dimAmount = 0.0F
        }
    }

    /**
     * 配置是否可以取消弹窗.
     * true：可以取消，默认值；false：不可取消
     */
    fun applyCancelable(value: Boolean) {
        canCancelable = value
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