@file:Suppress("unused")

package com.classic.birthday.data.local

import androidx.annotation.DrawableRes
import com.classic.birthday.R

/**
 * 预置头像映射
 *
 * 说明：之前 [User.photoResId] 直接把 R.drawable.xxx 这种「资源 ID」存进数据库。
 * 资源 ID 并不是稳定值——它在每次构建（尤其升级到 AGP 9 后 app 模块使用非 final 的 R 类、
 * 以及开启 nonTransitiveRClass 之后）都可能变化，导致旧数据里存的 ID 指向错误/不存在的图片，
 * 预置的默认头像因此不显示。
 *
 * 这里改为在数据库中存储「稳定的头像编码」（见下方常量），展示时再通过 [resOf] 映射成真正的
 * drawable 资源。历史遗留的非法值（旧的资源 ID）会被归一化为默认头像。
 *
 * @author LiuBin
 */
object Avatar {
    /** 默认头像 */
    const val DEFAULT = 0
    const val JY = 1
    const val BS = 2
    const val RL = 3

    /**
     * 头像编码 -> drawable 资源
     */
    @DrawableRes
    fun resOf(code: Int): Int = when (code) {
        JY -> R.drawable.ic_photo_jy
        BS -> R.drawable.ic_photo_bs
        RL -> R.drawable.ic_photo_rl
        else -> R.drawable.ic_default_photo
    }
}
