@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.classic.core.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.classic.core.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * BottomSheetDialogFragment
 *
 * > `BottomSheetDialogFragment` 是 `AppCompatFragment` 的子类，这意味着您需要使用 `Activity.getSupportFragmentManager()`
 * > 请勿调用 `setOnCancelListener` 或者 `setOnDismissListener`，如有需要可以重写 `onCancel(DialogInterface)` 或者 `onDismiss(DialogInterface)`
 *
 * @author Classic
 * @date 2021/5/26 9:23 上午
 */
abstract class CoreBottomSheetDialogFragment : BottomSheetDialogFragment() {
    protected lateinit var appContext: Context
    private var weakContext: WeakReference<AppCompatActivity>? = null
    private var canCancelable = true

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
    fun fm() = childFragmentManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag(label())
        appContext = context.applicationContext
        weakContext = WeakReference(context as AppCompatActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onObtainParams()
        try {
            val behavior = BottomSheetBehavior.from(view.parent as View)
            configBottomSheetBehavior(behavior)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun configBottomSheetBehavior(behavior: BottomSheetBehavior<View>) {
        // 控制默认不折叠
        behavior.skipCollapsed = true
        // 控制可以全部收起
        behavior.isHideable = true
        // // 控制最小折叠高度
        // behavior.peekHeight = xxx
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
            window?.apply {
                // 设置BottomSheetDialogFragment背景颜色
                findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.setBackgroundResource(R.color.transparent)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                // 部分机型弹出DialogFragment会有一圈边距，这里做一下兼容。
                decorView.setPadding(0, 0, 0, 0)
                isCancelable = canCancelable
                // val params = attributes
                // params.dimAmount = 0F
                // attributes = params
            }
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
     * 配置是否可以取消弹窗.
     * true：可以取消，默认值，false：不可取消
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