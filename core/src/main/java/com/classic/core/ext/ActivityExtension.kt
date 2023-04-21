@file:Suppress("unused")

package com.classic.core.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.core.os.bundleOf
import java.io.Serializable

/**
 * Activity extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
fun Context.start(cls: Class<*>) {
    val isNotActivity = this !is Activity
    startActivity(Intent(this, cls).apply {
        // 解决非Activity context在Android P以上的系统，
        // 抛出异常：Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag
        if (isNotActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    })
}

/**
 * Activity跳转扩展
 *
 * ```
 * // Sample
 * start(TargetActivity::class.java,
 *     KEY_STATUS to status,
 *     KEY_XXX to xxx,
 *     ...
 * ) {
 *     addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
 *     addCategory(Intent.CATEGORY_DEFAULT)
 *     component = ComponentName("componentPackageName", "componentClassName")
 * }
 * ```
 */
inline fun Context.start(
    cls: Class<out Activity>,
    vararg pairs: Pair<String, Any?>,
    crossinline block: Intent.() -> Unit = {}
) {
    val isNotActivity = this !is Activity
    startActivity(Intent(this, cls).apply {
        // 解决非 Activity context 在 Android P 以上的系统，
        // 抛出异常：Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag
        if (isNotActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        putExtras(bundleOf(*pairs))
        block()
    })
}

/**
 * 界面是否已销毁
 */
fun Activity.isDestroy() = isFinishing || isDestroyed

/** 操作是否成功 */
fun ActivityResult.isResultOk(): Boolean = Activity.RESULT_OK == resultCode

/**
 * `setResult` 返回单个数据时，默认使用这个Key
 */
const val EXTRA_RESULT = "result"

/** 关闭目标页面，取消本次操作 **/
fun Activity.cancelAndFinish() {
    setResult(Activity.RESULT_CANCELED)
    finish()
}
/** 关闭目标页面，并返回结果 **/
fun Activity.setResultAndFinish(intent: Intent? = null) {
    setResult(Activity.RESULT_OK, intent)
    finish()
}
fun Activity.setResultAndFinish(value: Int) {
    setResult(Activity.RESULT_OK, Intent().also { it.putExtra(EXTRA_RESULT, value) })
    finish()
}
fun Activity.setResultAndFinish(value: Long) {
    setResult(Activity.RESULT_OK, Intent().also { it.putExtra(EXTRA_RESULT, value) })
    finish()
}
fun Activity.setResultAndFinish(value: String) {
    setResult(Activity.RESULT_OK, Intent().also { it.putExtra(EXTRA_RESULT, value) })
    finish()
}
fun Activity.setResultAndFinish(value: Serializable) {
    setResult(Activity.RESULT_OK, Intent().also { it.putExtra(EXTRA_RESULT, value) })
    finish()
}
fun Activity.setResultAndFinish(vararg pairs: Pair<String, Any?>) {
    setResult(Activity.RESULT_OK, Intent().apply { putExtras(bundleOf(*pairs)) })
    finish()
}

/**
 * 双击返回键退出应用程序
 *
 * @param firstBackToastText 第一次点击返回键的提示语
 * @param delayMillis 两次有效点击的间隔时间
 * @param onExitApp 退出应用操作
 */
inline fun ComponentActivity.registerDoubleClickExitApp(
    firstBackToastText: String = "再按一次返回键退出应用",
    delayMillis: Long = 2000,
    crossinline onExitApp: () -> Unit
) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        private var lastBackTime: Long = 0
        override fun handleOnBackPressed() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackTime > delayMillis) {
                if (firstBackToastText.isNotEmpty()) toast(firstBackToastText)
                lastBackTime = currentTime
            } else {
                onExitApp()
            }
        }
    })
}
/**
 * 双击返回键将应用切换到后台
 */
fun ComponentActivity.registerDoubleClickMoveAppToBackground() {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveTaskToBack(false)
        }
    })
}