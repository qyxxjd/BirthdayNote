@file:Suppress("unused")

package com.classic.core.ext

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.classic.core.data.PackageConst
import java.lang.reflect.Field
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * Context extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
fun Context.getInputMethodManager(): InputMethodManager =
    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
/**
 * 显示软键盘
 */
fun Context.showKeyboard(view: View?, flag: Int = InputMethodManager.SHOW_FORCED) {
    getInputMethodManager().showSoftInput(view, flag)
}
/**
 * 关闭软键盘
 */
fun Context.hideKeyboard(view: View?, flag: Int = 0) {
    getInputMethodManager().hideSoftInputFromWindow(view?.windowToken, flag)
}
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus)
}

fun Context.packageInfo(): PackageInfo = packageManager.getPackageInfo(packageName, 0)
fun Context.versionName(): String = packageInfo().versionName
fun Context.versionCode(): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    packageInfo().longVersionCode
} else {
    @Suppress("DEPRECATION")
    packageInfo().versionCode.toLong()
}

fun Context.inflate(layoutResId: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View =
    LayoutInflater.from(this).inflate(layoutResId, parent, attachToRoot)

fun Context.color(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

/**
 * - [Link1](https://saurabharora.dev/2020-10-20-appcompatresources-vs-contextcompat-resourcescompat/)
 * - [Link2](https://stackoverflow.com/questions/43004886/resourcescompat-getdrawable-vs-appcompatresources-getdrawable)
 */
fun Context.drawable(resId: Int): Drawable? {
    if (resId == 0) return null
    return AppCompatResources.getDrawable(this, resId)
}

fun Context.string(resId: Int): String {
    return resources.getString(resId)
}

fun Context.string(resId: Int, vararg params: Any): String {
    return resources.getString(resId, *params)
}

/**
 * 是否安装某个应用
 *
 * @param packageName 应用包名
 * [PackageConst]
 */
fun Context.isPackageInstalled(packageName: String): Boolean {
    var packageInfo: PackageInfo?
    try {
        packageInfo = packageManager.getPackageInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        packageInfo = null
        e.printStackTrace()
    }
    return packageInfo != null
}

fun Context?.getMetaData(key: String, defaultValue: String = EMPTY): String {
    if (null == this || key.isEmpty()) return defaultValue
    try {
        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData?.let {
            if (it.containsKey(key)) {
                val value = it.getString(key, defaultValue)
                if (value.isNotEmpty()) return value
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return defaultValue
}

fun Context.statusBarHeight(): Int {
    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    //     val windowMetrics = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
    //     val windowInsets = windowMetrics.windowInsets
    //     val insets: Insets =
    //         windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout())
    //     return insets.top
    // }
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/** 获取剪切板文本 */
fun Context.getClipText(): String {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    if (null == clipboardManager || !clipboardManager.hasPrimaryClip()) {
        return ""
    }
    val clipData = clipboardManager.primaryClip
    if (null == clipData || clipData.itemCount < 1) {
        return ""
    }
    val clipText = clipData.getItemAt(0)?.text ?: ""
    return clipText.toString()
}

/** 清空剪切板文本 */
fun Context.clearClipText() {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    clipboardManager?.setPrimaryClip(ClipData.newPlainText(null, ""))
}

/** 网络是否连接 */
fun Context.isNetworkConnected(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        manager.getNetworkCapabilities(manager.activeNetwork)?.apply {
            return hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || // 移动网络
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||  // Wifi
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) // 以太网
        }
    } else {
        @Suppress("DEPRECATION")
        manager.activeNetworkInfo?.apply {
            return isConnected
        }
    }
    return false
}

/** 网络类型 */
enum class NetworkType {
    /** Wifi */
    WIFI,
    /** 移动网络 */
    CELLULAR,
    /** 蓝牙 */
    BLUETOOTH,
    /** 以太网 */
    ETHERNET,
    /** 其它 */
    OTHER,
    /** 未连接网络 */
    NONE
}
/** 获取网络类型 */
fun Context.getNetworkType(): NetworkType {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        manager.getNetworkCapabilities(manager.activeNetwork)?.apply {
            return when {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
                hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> NetworkType.BLUETOOTH
                else -> NetworkType.NONE
            }
        }
    } else {
        @Suppress("DEPRECATION")
        manager.activeNetworkInfo?.apply {
            return when(type) {
                ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
                ConnectivityManager.TYPE_MOBILE -> NetworkType.CELLULAR
                ConnectivityManager.TYPE_ETHERNET -> NetworkType.ETHERNET
                ConnectivityManager.TYPE_BLUETOOTH -> NetworkType.BLUETOOTH
                else -> NetworkType.NONE
            }
        }
    }
    return NetworkType.NONE
}

// /**
//  * 获取局域网信息
//  */
// fun Context.getWifiDetail(): DhcpInfo? {
//     val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//     if (isPermissionGranted(Manifest.permission.ACCESS_WIFI_STATE)) {
//         return wm.dhcpInfo
//     }
//     return null
// }
// @SuppressLint("HardwareIds")
// fun Context.printWifiDetail() {
//     if (isPermissionGranted(Manifest.permission.ACCESS_WIFI_STATE)) {
//         val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//         L.d("DhcpInfo")
//         wm.dhcpInfo?.let {
//             L.d("IP 地址: ${it.ipAddress.toIP()}")
//             L.d("网   关: ${it.gateway.toIP()}")
//             L.d("子网掩码: ${it.netmask.toIP()}")
//             L.d("服务器地址: ${it.serverAddress.toIP()}")
//             L.d("DNS1: ${it.dns1.toIP()}")
//             L.d("DNS2: ${it.dns2.toIP()}")
//         }
//         L.d("WifiInfo")
//         wm.connectionInfo?.let {
//             L.d("IP 地址: ${it.ipAddress.toIP()}")
//             L.d("SSID: ${it.ssid}")
//             L.d("BSSID: ${it.bssid}")
//             L.d("MAC address: ${it.macAddress}")
//             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                 L.d("Wifi standard: ${it.wifiStandard}")
//                 L.d("RSSI: ${wm.calculateSignalLevel(it.rssi)}")
//             } else {
//                 @Suppress("DEPRECATION")
//                 L.d("RSSI: ${WifiManager.calculateSignalLevel(it.rssi, 10)}")
//             }
//             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                 L.d("Fqdn: ${it.passpointFqdn.safeText()}")
//                 L.d("ProviderFriendlyName: ${it.passpointProviderFriendlyName}")
//             }
//         }
//     }
// }
fun Int.toIP(): String =
    InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this).array()).hostAddress.safeText()


/**
 * 修复输入法内存泄漏问题
 */
fun Context.fixInputMethodMemoryLeak() {
    val inputMethodManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val viewArr = arrayOf("mCurRootView", "mServedView", "mNextServedView")
    var field: Field?
    var fieldObj: Any?
    for (view in viewArr) {
        try {
            field = inputMethodManager.javaClass.getDeclaredField(view)
            if (null != field && !field.isAccessible) {
                field.isAccessible = true
            }
            fieldObj = field.get(inputMethodManager)
            if (fieldObj != null && fieldObj is View) {
                if (fieldObj.context === this) {
                    //注意需要判断View关联的Context是不是当前Activity，否则有可能造成正常的输入框输入失效
                    field.set(inputMethodManager, null)
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}