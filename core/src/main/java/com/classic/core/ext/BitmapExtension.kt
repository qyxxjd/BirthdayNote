@file:Suppress("unused")

package com.classic.core.ext

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.classic.core.data.MIME
import timber.log.Timber
import java.io.*


/**
 * Bitmap extensions
 *
 * @author Classic
 * @date 2019-05-20 15:48
 */

fun Bitmap?.isNotEmpty(): Boolean {
    return null != this && this.byteCount > 0
}
fun Bitmap?.size(): Int {
    return this?.byteCount ?: 0
}
/** 安全回收Bitmap */
fun Bitmap?.safeRecycle() {
    try {
        if (null != this && !isRecycled) recycle()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}
/** 文件转Bitmap */
fun File.toBitmap(): Bitmap? {
    return BitmapFactory.decodeFile(this.absolutePath)
}

/** Drawable转Bitmap */
fun Drawable.toBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    draw(canvas)
    return bitmap
}

/** Bitmap 转 ByteArray */
fun Bitmap?.toByteArray(format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100): ByteArray? {
    if (null == this) return null
    val os = ByteArrayOutputStream()
    compress(format, quality, os)
    return os.toByteArray()
}

/** 保存Bitmap */
fun Bitmap.save(
    file: File,
    bitmapFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    bitmapQuality: Int = 100
): Boolean {
    var stream: OutputStream? = null
    return try {
        stream = FileOutputStream(file)
        this.compress(bitmapFormat, bitmapQuality, stream)
        stream.flush()
        stream.safeClose()
        true
    } catch (e: IOException){
        e.printStackTrace()
        false
    } finally {
        stream.safeClose()
    }
}

/** 创建相册文件 */
fun String.createGalleryImage(): File {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 9.0开始新版API，只需要一个定义文件名即可
        File(this)
    } else {
        // Android 9.0之前，需要定义文件的完整路径
        @Suppress("DEPRECATION")
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        File(dir, this)
    }
}
/**
 * 保存图片到系统相册
 *
 * @param file 注意：创建文件需要区分版本，参考[createGalleryImage]
 * [Manifest.permission.WRITE_EXTERNAL_STORAGE]
 */
fun Bitmap.saveImageToGallery(
    context: Context, file: File,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    mime: String = MIME.IMAGE_PNG,
    quality: Int = 100
): Boolean {
    val values = ContentValues().apply {
        put(MediaStore.Images.ImageColumns.DISPLAY_NAME, file.name)
        put(MediaStore.Images.ImageColumns.MIME_TYPE, mime)
        // 将图片的拍摄时间设置为当前的时间
        put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.MediaColumns.IS_PENDING, true)
        } else {
            @Suppress("DEPRECATION")
            put(MediaStore.Images.ImageColumns.DATA, file.absolutePath)
        }
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        val out = resolver.openOutputStream(uri)
        compress(format, quality, out)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, false)
            resolver.update(uri, values, null, null)
        }
        out.safeClose()
        return true
    }
    return false
}

/**
 * 等比例缩放
 *
 * @param targetWidth 缩放至目标宽度
 */
fun Bitmap.ratioResize(targetWidth: Int = 1080, printLog: Boolean = true): Bitmap? {
    if (this.width <= targetWidth) return this
    return try {
        if (printLog) d("当前图片尺寸(处理前)：${desc()}")
        // 获取屏幕宽度和图片宽度的缩放比例
        val scale = targetWidth.toFloat() / this.width
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        // 使用缩放比例，创建新的Bitmap
        val result = Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
        if (printLog) d("当前图片尺寸(处理后)：${result.desc()}")
        result
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 等比例缩放至目标大小以内
 *
 * @param maxSize 缩放至目标大小以内
 * @param targetWidth 起始宽度
 * @param offset 宽度递减的偏移量
 */
fun Bitmap.ratioResize(maxSize: Long, targetWidth: Int, offset: Int): Bitmap? {
    d("当前图片尺寸(处理前)：${desc()}")
    if (this.size() < maxSize) return this
    return try {
        var currentWidth = targetWidth
        var resizeBitmap: Bitmap? = ratioResize(currentWidth, false)
        while (currentWidth > offset && resizeBitmap.size() > maxSize) {
            if (currentWidth > offset) currentWidth -= offset
            resizeBitmap = ratioResize(currentWidth, false)
            d("当前图片尺寸(处理中)：${resizeBitmap?.desc()}")
        }
        d("当前图片尺寸(处理后)：${resizeBitmap?.desc()}")
        resizeBitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 微信小程序封面最大尺寸128kb
 */
const val MINI_PROGRAM_BITMAP_MAX_SIZE = 128 * 1024L
/**
 * 等比例缩放
 *
 * > 微信小程序分享专用，大小不能超过128kb
 */
fun Bitmap.ratioResizeToWeChatMiniProgram(): Bitmap? =
    ratioResize(MINI_PROGRAM_BITMAP_MAX_SIZE, 200, 20)

/**
 * Bitmap压缩
 *
 * @param maxSize 压缩至目标大小以下
 */
fun Bitmap?.compress(maxSize: Long, printLog: Boolean = false): ByteArray? {
    if (null == this) return null
    if (printLog) d("compress:当前图片尺寸(处理前)：${desc()}")
    return try {
        val os = ByteArrayOutputStream()
        var options = 100
        compress(Bitmap.CompressFormat.JPEG, options, os)
        while (os.toByteArray().size > maxSize && options > 10) {
            os.reset()
            compress(Bitmap.CompressFormat.JPEG, options, os)
            options -= 10
        }
        if (printLog) d("compress:当前图片尺寸(处理后)：${os.toByteArray().size / 1024}kb")
        return os.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun Bitmap?.compressByMatrix(scale: Float = 0.8F, printLog: Boolean = false): Bitmap? {
    if (null == this) return null
    return try {
        if (printLog) d("compressByMatrix:当前图片尺寸(处理前)：${desc()}")
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        // 使用缩放比例，创建新的Bitmap
        val result = Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
        if (printLog) d("compressByMatrix:当前图片尺寸(处理后)：${result.desc()}")
        result
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun File?.compressByInSampleSize(inSampleSize: Int = 2, printLog: Boolean = false): Bitmap? {
    if (null == this || !this.exists()) return null
    if (printLog) d("compressByInSampleSize:当前图片尺寸(处理前)：${this.length() / 1024}kb")
    return try {
        val options = BitmapFactory.Options()
        options.inSampleSize = inSampleSize
        val bitmap = BitmapFactory.decodeFile(absolutePath, options)
        if (printLog) d("compressByInSampleSize:当前图片尺寸(处理后)：${bitmap.desc()}")
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Bitmap描述信息
 */
fun Bitmap.desc(): String = "${width}x${height}, size:${byteCount / 1024}kb"

/**
 * 网络.9图片转Drawable
 *
 * > .9图不能直接放到服务器上，必须经过Android的工具编译，然后下载图片后需要用NinePatchDrawable类重新处理后才生效。
 */
fun Bitmap.toNonePatchDrawable(context: Context): Drawable? {
    try {
        if (NinePatch.isNinePatchChunk(ninePatchChunk)) {
            return NinePatchDrawable(context.resources, this, ninePatchChunk,
                Rect(), null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

private fun d(content: String) {
    Timber.d(content)
}