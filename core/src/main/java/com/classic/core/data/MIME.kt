@file:Suppress("SpellCheckingInspection", "unused", "MemberVisibilityCanBePrivate")

package com.classic.core.data

/**
 * 常用的文件MIME类型
 *
 * @author LiuBin
 * @date 2021/12/7 16:45
 */
object MIME {

    // 通用文件，不限制类型
    const val FILE = "*/*"
    const val APPLICATION = "application/*"
    // 字体
    const val FONT = "font/*"

    // Android应用程序
    const val APK = "application/vnd.android.package-archive"
    // 压缩文件(.zip .rar .7z)
    const val ZIP = "application/zip"
    const val ZIP_7Z = "application/x-7z-compressed"
    const val RAR = "application/x-rar-compressed"

    // 文本
    const val TEXT = "text/*"
    const val HTML = "text/html"

    // 图片
    const val IMAGE = "image/*"
    const val IMAGE_PNG = "image/png"
    const val IMAGE_JPEG = "image/jpeg"

    // 音视频
    const val AUDIO = "audio/*"
    const val VIDEO = "video/*"

    // Office
    const val PDF = "application/pdf"
    const val DOC = "application/msword"
    const val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    const val EXCEL_XLS = "application/vnd.ms-excel"
    const val EXCEL_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    const val PPT = "application/vnd.ms-powerpoint"
    const val PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation"

    // 常用类型
    val COMMON = listOf(FILE, APPLICATION, TEXT, IMAGE, AUDIO, VIDEO)

}