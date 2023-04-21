@file:Suppress("unused")

package com.classic.core.ext

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.*

/**
 * Animation extensions
 *
 * @author Classic
 * @date 2021-03-03 15:48
 */

fun Animation?.release() {
    this?.let {
        it.setAnimationListener(null)
        it.cancel()
    }
}

fun Animator?.release() {
    this?.let {
        it.removeAllListeners()
        it.cancel()
    }
}

fun View?.releaseAnim() {
    this?.let {
        it.animate().cancel()
        it.animation?.reset()
        it.clearAnimation()
        it.animation = null
    }
}

/**
 * 旋转动画
 *
 * @param duration 时长
 * @param repeatCount 重复次数
 * @param repeatMode 模式
 */
fun View?.startRotateAnim(duration: Long = 3000L, repeatCount: Int = -1, repeatMode: Int = Animation.RESTART) {
    releaseAnim()
    this?.let {
        val rotateAnimation = RotateAnimation(
            0F, 360F,
            Animation.RELATIVE_TO_SELF, 0.5F,
            Animation.RELATIVE_TO_SELF, 0.5F
        )
        rotateAnimation.duration = duration
        rotateAnimation.fillAfter = true
        rotateAnimation.repeatMode = repeatMode
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.repeatCount = repeatCount
        it.startAnimation(rotateAnimation)
    }
}

/**
 * 缩放动画
 *
 * @param duration 动画时长
 * @param values 缩放参数
 */
@SuppressLint("Recycle")
fun View?.startZoomAnim(duration: Long = 400L, listener: Animator.AnimatorListener? = null, vararg values: Float) {
    if (null == this) return
    releaseAnim()
    val animScaleX = ObjectAnimator.ofFloat(this, "scaleX", *values)
    val animScaleY = ObjectAnimator.ofFloat(this, "scaleY", *values)
    val animSet = AnimatorSet()
    // animSet.interpolator = OvershootInterpolator()
    animSet.playTogether(animScaleX, animScaleY)
    animSet.duration = duration
    animSet.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) {
            listener?.onAnimationStart(animator)
        }
        override fun onAnimationEnd(animator: Animator) {
            listener?.onAnimationEnd(animator)
            releaseAnim()
        }
        override fun onAnimationCancel(animator: Animator) {
            listener?.onAnimationCancel(animator)
            // releaseAnim()
        }
        override fun onAnimationRepeat(animator: Animator) {
            listener?.onAnimationRepeat(animator)
        }
    })
    animSet.start()
}

/**
 * 平移动画（从当前View移动到目标View）
 *
 * @param targetView 目标View
 * @param duration 时长
 * @param repeatMode 模式
 * @param interpolator 动画插值器
 * @param listener 动画监听
 */
fun View?.moveToTargetView(
    targetView: View, duration: Long = 1000L, repeatMode: Int = Animation.RESTART,
    interpolator: BaseInterpolator = DecelerateInterpolator(),
    listener: Animation.AnimationListener? = null
    ) {
    if (null == this) return
    // val startLocation = intArrayOf(0, 0)
    // this.getLocationOnScreen(startLocation)
    // val startX = startLocation[0].toFloat()
    // val startY = startLocation[1].toFloat()


    visible()
    val location = intArrayOf(0, 0)
    targetView.getLocationOnScreen(location)
    val targetX = location[0].toFloat()
    val targetY = location[1].toFloat()
    val animation = TranslateAnimation(0F, -(x - targetX), 0F, -(y - targetY))
    // val animation = TranslateAnimation(startX, -(x - targetX), startY, -(y - targetY))
    // val animation = TranslateAnimation(0F, -(startX - targetX), 0F, -(startY - targetY))
    animation.repeatMode = repeatMode
    animation.duration = duration
    animation.interpolator = interpolator
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) { listener?.onAnimationStart(animation) }
        override fun onAnimationEnd(animation: Animation) {
            gone()
            releaseAnim()
            listener?.onAnimationEnd(animation)
        }
        override fun onAnimationRepeat(animation: Animation) { listener?.onAnimationRepeat(animation) }
    })
    startAnimation(animation)
}