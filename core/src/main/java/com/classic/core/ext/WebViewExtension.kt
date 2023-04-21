@file:Suppress("unused")

package com.classic.core.ext

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * WebView extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
/** 应用基础配置 */
@SuppressLint("SetJavaScriptEnabled")
fun WebView.applyConfig(supportZoom: Boolean = true) {
    with(settings) {
        // userAgentString += "_${App.config().label()}_android_${Build.VERSION.SDK_INT}_${BuildConfig.VERSION_NAME}_"
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        domStorageEnabled = true   // 设置可以使用localStorage
        // 允许访问文件
        allowFileAccess = true
        databaseEnabled = true
        // 屏幕缩放
        setSupportZoom(supportZoom)
        builtInZoomControls = supportZoom
        displayZoomControls = supportZoom
        useWideViewPort = true     // 设置自适应屏幕大小
        loadWithOverviewMode = true// 设置自适应屏幕大小
        mediaPlaybackRequiresUserGesture = false     // 允许自动播放
        // cacheMode = WebSettings.LOAD_NO_CACHE
        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        if (Build.VERSION.SDK_INT >= 26) {
            safeBrowsingEnabled = false
        }
    }
}

/** 设置背景透明 */
fun WebView?.applyBackgroundTransparent() {
    this?.apply {
        // 设置背景透明，两个方法缺一不可
        setBackgroundColor(Color.TRANSPARENT)
        background.alpha = 0
    }
}

/** 释放资源 */
fun WebView?.release(clearCache: Boolean = true) {
    if (null == this) return
    try {
        parent?.let {
            (it as ViewGroup).removeView(this)
        }
        webChromeClient = null
        stopLoading()
        clearHistory()
        if (clearCache) clearCache(true)
        // loadUrl("about:blank")
        // removeAllViews()
        destroy()
    } catch (e: Exception) {}
}

/** 常用的第三方App scheme */
@Suppress("SpellCheckingInspection")
fun String?.hasThirdScheme(): Boolean {
    this?.let {
        // 微信、支付宝、京东、拼多多、大淘客、QQ、美团 协议适配
        if (it.startsWith("weixin://") ||
            it.startsWith("alipay://") ||
            it.startsWith("alipays://") ||
            // 大淘客
            it.startsWith("tbopen://") ||
            // 京东
            it.startsWith("openapp.jdmoble://") ||
            it.startsWith("pinduoduo://") ||
            it.startsWith("mqqapi://") ||
            it.startsWith("imeituan://")) {
            return true
        }
    }
    return false
}