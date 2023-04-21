@file:Suppress("unused")

package com.classic.core.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.classic.core.data.MIME
import com.classic.core.data.PackageConst
import java.io.File
import java.net.URLEncoder

/**
 * Intent extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
fun Intent?.isValid(context: Context): Boolean {
    if (null == this) return false
    return context.packageManager.resolveActivity(this, PackageManager.MATCH_DEFAULT_ONLY) != null
}
fun Intent?.boolean(key: String, defaultValue: Boolean = false): Boolean {
    var value = defaultValue
    if (null != this && hasExtra(key)) value = this.getBooleanExtra(key, defaultValue)
    return value
}
fun Intent?.int(key: String, defaultValue: Int = 0): Int {
    var value = defaultValue
    if (null != this && hasExtra(key)) value = this.getIntExtra(key, defaultValue)
    return value
}
fun Intent?.long(key: String, defaultValue: Long = 0): Long {
    var value = defaultValue
    if (null != this && hasExtra(key)) value = this.getLongExtra(key, defaultValue)
    return value
}
fun Intent?.float(key: String, defaultValue: Float = 0F): Float {
    var value = defaultValue
    if (null != this && hasExtra(key)) value = this.getFloatExtra(key, defaultValue)
    return value
}
fun Intent?.double(key: String, defaultValue: Double = 0.0): Double {
    var value = defaultValue
    if (null != this && hasExtra(key)) value = this.getDoubleExtra(key, defaultValue)
    return value
}
fun Intent?.string(key: String): String {
    var value = ""
    if (null != this && hasExtra(key)) value = this.getStringExtra(key) ?: ""
    return value
}
fun <T> Intent?.serializable(key: String): T? {
    if (null == this) return null
    return if (hasExtra(key)) {
        @Suppress("UNCHECKED_CAST")
        this.getSerializableExtra(key) as T?
    } else null
}
fun <T> Intent?.parcelable(key: String): T? {
    if (null == this) return null
    return if (hasExtra(key)) {
        @Suppress("UNCHECKED_CAST")
        this.getParcelableExtra<Parcelable>(key) as T?
    } else null
}
/** 根据包名获取启动Intent */
fun Context.getLaunchIntent(packageName: String): Intent? {
    return packageManager?.getLaunchIntentForPackage(packageName)?.also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
}
/** 打开App */
fun Context.openAppForPackage(packageName: String, errorHint: String = "未安装应用") {
    try {
        getLaunchIntent(packageName)?.let {
            startActivity(it)
        } ?: toast(errorHint)
    } catch (e: Exception) {
        toast(errorHint)
    }
}
/** 打开App */
fun Context.openApp(
    packageName: String,
    launcherClassFullName: String,
    errorHint: String = "未安装应用") {
    try {
        val intent = Intent()
        intent.component = ComponentName(packageName, launcherClassFullName)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        if (errorHint.isNotEmpty()) toast(errorHint)
    }
}

/**
 * 安装应用
 *
 * 注意事项：必须添加 `android.permission.REQUEST_INSTALL_PACKAGES` 权限
 *
 * @param authority FileProvider
 * @param file 安装的应用文件
 * @param launcher 申请安装权限结果回调
 */
@SuppressLint("ObsoleteSdkInt")
fun Activity.installApp(authority: String, file: File, launcher: ActivityResultLauncher<Intent>?) {
    val uri: Uri? = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Uri.fromFile(file)
    } else {
        FileProvider.getUriForFile(this, authority, file)
    }
    installApp(uri, launcher)
}
/**
 * 安装应用
 *
 * 注意事项：必须添加 `android.permission.REQUEST_INSTALL_PACKAGES` 权限
 *
 * @param uri 安装的应用文件Uri
 */
@SuppressLint("ObsoleteSdkInt")
fun Context.installApp(uri: Uri?, launcher: ActivityResultLauncher<Intent>? = null) {
    if (null == uri) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        // 低于Android7.0: 用旧版Api直接安装
        // 注意事项：`Uri.fromFile`可能为空，需要兼容
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, MIME.APK)
        startActivity(intent)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1 &&
        !packageManager.canRequestPackageInstalls()) {
        // 8.0 、 8.1，并且没有安装权限，去申请安装权限
        launcher?.launch(
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${packageName}"))
        )
    } else {
        // 其它情况，用新版Api直接安装
        @Suppress("DEPRECATION")
        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, MIME.APK)
        startActivity(intent)
    }
}
/** 打开系统设置页面 */
fun Activity.openSettings() {
    val intent = Intent(Settings.ACTION_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}
/** 打开系统时间设置页面 */
fun Activity.openDateSettings() {
    val intent = Intent(Settings.ACTION_DATE_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}
/** 打开应用详情页面 */
fun Activity.openAppDetail() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", packageName, null)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}
/** 进入拨号界面 */
fun Activity.openDial(phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast("未找到可用的应用程序")
    }
}
/** 进入发送邮件界面 */
fun Activity.openSendEmail(email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast("未找到可用的应用程序")
    }
}

private const val MIME_DIR = "resource/folder"
/**
 * 生成打开文件夹Intent
 *
 * 注意事项：
 * - 非通用MIME类型，暂时没有好的替代方案
 * - 使用 `resolveActivity` 校验Intent会出现找不到对应组件
 */
fun Uri?.openDirIntent(mime: String = MIME_DIR): Intent? {
    if (null == this) return null
    return Intent(Intent.ACTION_VIEW).also {
        it.setDataAndType(this, mime)
        it.addCategory(Intent.CATEGORY_DEFAULT)
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
fun Context.openDir(path: String?, mime: String = MIME_DIR) { openDir(Uri.parse(path), mime) }
fun Context.openDir(uri: Uri?, mime: String = MIME_DIR) {
    try {
        if (null == uri) toast("路径为空")
        uri?.openDirIntent(mime)?.let {
            startActivity(it)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        toast("打开文件管理器失败：${e.message}")
    }
}
/**
 * 根据包名，使用目标客户端打开链接
 */
fun Context.openClientUrl(url: String, packageName: String) {
    if (url.isEmpty()) {
        toast("url为空")
        return
    }
    try {
        if (isPackageInstalled(packageName)) {
            val intent = Intent()
            @Suppress("SpellCheckingInspection")
            intent.`package` = packageName
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            startActivity(intent)
        } else {
            val app = PackageConst.findAppName(packageName)
            val label = if (app.isEmpty()) "未安装应用" else "[${app}]未安装"
            longToast("${label}，正在使用系统浏览器打开...")
            openSystemBrowser(url)
        }
    } catch (e: Throwable) {
        // 未安装应用，再用系统浏览器打开一次
        openSystemBrowser(url)
    }
}
/**
 * 根据包名，使用目标客户端打开链接(大淘客专用)
 */
fun Context.openDaTaoKeUrl(url: String, packageName: String, showToast: Boolean = false): Boolean {
    if (url.isEmpty()) {
        if (showToast) toast("url为空")
        return false
    }
    return try {
        val intent = Intent()
        @Suppress("SpellCheckingInspection")
        intent.`package` = packageName
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        startActivity(intent)
        true
    } catch (e: Throwable) {
        // 未安装应用，再用系统浏览器打开一次
        openSystemBrowser(url, true, showToast)
    }
}

/** 系统浏览器打开Url */
fun Context.openSystemBrowser(url: String?, addNewTask: Boolean = true, showToast: Boolean = true): Boolean {
    if (url.isNullOrEmpty()) {
        if (showToast) toast("url为空")
        return false
    }
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (addNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        true
    } catch (e: Throwable) {
        if (showToast) toast("不支持的链接类型：$url")
        false
    }
}

@SuppressLint("BatteryLife")
fun Context.createIgnoreBatteryOptimizationsIntent(): Intent =
    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName"))
/** 忽略电池优化 */
fun Context.requestIgnoreBatteryOptimizations(addNewTask: Boolean = true, errorHint: String = EMPTY): Boolean {
    return try {
        val intent = createIgnoreBatteryOptimizationsIntent()
        if (addNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        true
    } catch (e: Throwable) {
        e.printStackTrace()
        if (errorHint.isNotEmpty()) toast(errorHint)
        false
    }
}
/** 查下忽略电池优化状态 */
@SuppressLint("ObsoleteSdkInt")
fun Context.isIgnoringBatteryOptimizations(): Boolean {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager?
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager?.isIgnoringBatteryOptimizations(packageName) ?: false
    } else {
        true
    }
}

/**
 * 打开支付宝小程序
 *
 * @param appId 支付宝小程序appId
 * @param page 路径
 * @param params 路径参数
 */
@Suppress("SpellCheckingInspection")
fun Activity.openAliMiniProgram(appId: String, page: String, params: String) {
    val encoderParams = URLEncoder.encode(params, "UTF-8")
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("alipays://platformapi/startapp?appId=${appId}&page=${page}&query=${encoderParams}")
    )
    startActivity(intent)
}

// const val WX_PACKAGE_NAME = "com.tencent.mm"
// const val WX_LAUNCHER_UI = "com.tencent.mm.ui.LauncherUI"
// const val WX_SHARE_IMG_UI = "com.tencent.mm.ui.tools.ShareImgUI"
// const val WX_SHARE_TO_TIMELINE_UI = "com.tencent.mm.ui.tools.ShareToTimeLineUI"
/** 分享图片列表到微信好友 */
fun Activity.shareImageListToWeChat(list: List<File>, hint: String = "未安装微信") {
    try {
        val intent = Intent()
        val imageUris = ArrayList<Uri>()
        list.forEach {
            // 使用FileProvider分享多图到微信会提示错误：多文件分享仅支持照片格式
            // val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //     FileProvider.getUriForFile(this, App.config().provider(), it)
            // } else Uri.fromFile(it)
            // imageUris.add(uri)
            if (it.exists()) imageUris.add(Uri.fromFile(it))
        }
        intent.action = Intent.ACTION_SEND_MULTIPLE //设置分享行为
        intent.component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
        intent.type = "image/*" //设置分享内容的类型
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast(hint)
    }
}