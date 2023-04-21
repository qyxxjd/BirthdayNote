@file:Suppress("unused")

package com.classic.core.ext

import android.content.Context
import android.media.MediaScannerConnection
import com.classic.core.data.MIME
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * File extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */

/** 判断路径是否为本地文件 */
fun String?.hasLocalFile(): Boolean {
    if (isNullOrEmpty()) return false
    return startsWith("/", true) ||
           startsWith("content://", true) ||
           startsWith("file://", true)
}
/** 判断路径是否为本地绝对路径 */
fun String?.hasLocalAbsolutePath(): Boolean {
    if (isNullOrEmpty()) return false
    return startsWith("/", true)
}
/** 有效的文件 */
fun File?.isValid(): Boolean {
    return null != this && this.exists() && this.length() > 0
}
/** 检查文件的父级目录是否存在，不存在则创建 */
fun File?.checkMkdirs(): Boolean {
    this?.let {
        val file = if (it.isDirectory) it else it.parentFile
        if (!file.exists()) return file.mkdirs()
    }
    return false
}
/** 检查文件是否存在，不存在则创建 */
fun File?.checkCreate(deleteExistsFile: Boolean = false): Boolean {
    if (null == this) return false
    checkMkdirs()
    if (deleteExistsFile && exists()) delete()
    return if (!exists()) createNewFile() else true
}
/** 保存 InputStream 到文件 */
fun InputStream.saveToFile(file: File) {
    file.outputStream().use {
        this.copyTo(it)
    }
}
/** 保存字符串到文件 */
fun String.saveToFile(file: File) {
    file.outputStream().use {
        byteInputStream().copyTo(it)
    }
}
/** 异步删除文件 */
fun File.deleteAsync() {
    if (!exists()) return
    globalIOTask {
        // 如果是目录，递归删除目录。如果是文件，删除文件
        if (this.isDirectory) deleteRecursively() else delete()
    }
}
fun String.deleteFileAsync() {
    File(this).deleteAsync()
}

/** 通知系统，扫描文件 */
fun File?.scanFile(context: Context, mime: String = MIME.IMAGE,
                   listener: MediaScannerConnection.OnScanCompletedListener? = null) {
    context.scanFile(this, mime, listener)
}
/** 通知系统，扫描文件 */
fun Context.scanFile(file: File?, mime: String = MIME.IMAGE,
                     listener: MediaScannerConnection.OnScanCompletedListener? = null) {
    if (null == file || !file.exists()) return
    MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), arrayOf(mime), listener)
}
/**
 * 扫描文件列表
 *
 * @param paths 文件路径列表
 * @param mimeTypes 文件MIME列表
 * @param listener 扫描事件监听
 */
fun Context.scanFile(paths: Array<String>, mimeTypes: Array<String>,
                     listener: MediaScannerConnection.OnScanCompletedListener? = null) {
    MediaScannerConnection.scanFile(this, paths, mimeTypes, listener)
}

/**
 * 获取文件MD5
 */
fun File?.md5(): String {
    var file: RandomAccessFile? = null
    try {
        if (isValid()) {
            val md = MessageDigest.getInstance("MD5")
            file = RandomAccessFile(this, "r")
            val bytes = ByteArray(1024 * 1024 * 10)
            var len: Int
            while (file.read(bytes).also { len = it } != -1) {
                md.update(bytes, 0, len)
            }
            val bigInt = BigInteger(1, md.digest())
            var md5 = bigInt.toString(16)
            while (md5.length < 32) {
                md5 = "0$md5"
            }
            return md5
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        file.safeClose()
    }
    return EMPTY
}


/**
 * 压缩文件
 */
fun zip(files: List<File>, zipFilePath: String) {
    if (files.isEmpty()) return
    val zipFile = createFile(zipFilePath)
    val buffer = ByteArray(1024)
    var zipOutputStream: ZipOutputStream? = null
    var inputStream: FileInputStream? = null
    try {
        zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
        for (file in files) {
            if (!file.exists()) continue
            zipOutputStream.putNextEntry(ZipEntry(file.name))
            inputStream = FileInputStream(file)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > 0) {
                zipOutputStream.write(buffer, 0, len)
            }
            zipOutputStream.closeEntry()
        }
    } finally {
        inputStream?.close()
        zipOutputStream?.close()
    }
}
private fun createFile(filePath: String): File {
    val file = File(filePath)
    val parentFile = file.parentFile!!
    if (!parentFile.exists()) {
        parentFile.mkdirs()
    }
    if (!file.exists()) {
        file.createNewFile()
    }
    return file
}