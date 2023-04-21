package com.classic.core.manage

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

/**
 * Activity栈管理
 *
 * @author Classic
 * @date 2020/5/28 10:11 AM
 */
@Suppress("unused")
class ActivityStack private constructor() {
    companion object {
        const val TAG = "ActivityStack"
        private var INSTANCE: ActivityStack? = null
        @JvmStatic
        fun get(): ActivityStack = INSTANCE ?: ActivityStack().apply { INSTANCE = this }
    }
    private var activeSize: Int = 0
    private val stack = Stack<Activity>()

    fun size() = stack.size

    fun isEmpty() = stack.isEmpty()

    fun init(app: Application) {
        stack.clear()
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                stack.add(activity)
            }
            override fun onActivityStarted(activity: Activity) {
                if (isApplicationToBackground) onApplicationToFront()
                activeSize++
            }
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {
                activeSize--
                if (activeSize == 0) onApplicationToBackground()
            }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                stack.remove(activity)
            }
        })
    }

    /**
     * 获取栈顶activity
     */
    fun getTopActivity(): Activity? {
        return if (stack.isEmpty()) null else stack.peek()
    }

    /**
     * 关闭指定的activity
     */
    fun finishActivity(cls: Class<*>) {
        stack.forEach {
            if (cls == it.javaClass && !it.isFinishing) {
                it.finish()
                return
            }
        }
    }

    /**
     * 关闭除指定activity之外所有的activity
     */
    fun finishActivityExcept(cls: Class<*>) {
        stack.forEach {
            if (cls != it.javaClass && !it.isFinishing) {
                it.finish()
            }
        }
    }

    /**
     * 栈中是否包含指定的activity
     */
    fun containsActivity(cls: Class<*>): Boolean {
        stack.forEach {
            if (cls == it.javaClass && !it.isFinishing) {
                return true
            }
        }
        return false
    }

    /**
     * 关闭所有activity
     */
    fun finishAllActivity() {
        stack.iterator().forEach {
            if (!it.isFinishing) it.finish()
        }
        stack.clear()
    }

    /**
     * 关闭某个activity之前的所有activity
     */
    fun finishToActivity(cls: Class<*>): Boolean {
        while (stack.isNotEmpty()) {
            val item = stack.pop()
            if (cls == item.javaClass) {
                return true
            } else {
                if (!item.isFinishing) item.finish()
            }
        }
        return false
    }

    private var isApplicationToBackground = false
    private fun onApplicationToBackground() {
        // L.d(TAG, "应用进入后台")
        isApplicationToBackground = true
        // ApplicationStatusManage.get().dispatchApplicationToBackground()
    }
    private fun onApplicationToFront() {
        // L.d(TAG, "应用回到前台")
        isApplicationToBackground = false
        // ApplicationStatusManage.get().dispatchApplicationToFront()
    }
}

