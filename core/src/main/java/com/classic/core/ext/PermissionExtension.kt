@file:Suppress("unused")

package com.classic.core.ext

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat

/**
 * Permission extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */
// 相册权限：存储 + 摄像头
val GALLERY_PERMISSION = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA)
// 拍照权限
val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
// 存储权限
val STORAGE_PERMISSION = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE)
// 定位权限
val LOCATION_PERMISSION = arrayOf(
    // GPS定位权限
    Manifest.permission.ACCESS_FINE_LOCATION,
    // 网络定位权限
    Manifest.permission.ACCESS_COARSE_LOCATION)
// 音频录制权限
val RECORD_AUDIO_PERMISSION = arrayOf(
    // 录音
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE)

/** 是否已授权 */
fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
/** 存储权限是否已授权 */
fun Context.hasExternalPermission(): Boolean {
    return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
            isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}
/** 摄像头 */
fun Context.hasCameraPermission(): Boolean {
    return isPermissionGranted(Manifest.permission.CAMERA)
}
/** 存储 + 摄像头 */
fun Context.hasGalleryPermission(): Boolean {
    return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
            isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            isPermissionGranted(Manifest.permission.CAMERA)
}
/** 管理存储权限是否已授权 */
fun Context.hasManageExternalPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else hasExternalPermission()
}
/**
 * 蓝牙权限是否已授权
 *
 * > Android 6.0之后，想要扫描低功率蓝牙设备(BLE)，应用需要拥有访问设备位置的权限
 */
fun Context.hasBluetoothPermission(): Boolean {
    return isPermissionGranted(Manifest.permission.BLUETOOTH) &&
            isPermissionGranted(Manifest.permission.BLUETOOTH_ADMIN) &&
            hasLocationPermission()
}
/** 定位权限是否已授权 */
fun Context.hasLocationPermission(): Boolean {
    return isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
}
/** 音频录制权限是否已授权 */
fun Context.hasRecordAudioPermission(): Boolean {
    return isPermissionGranted(Manifest.permission.RECORD_AUDIO) && hasExternalPermission()
}
