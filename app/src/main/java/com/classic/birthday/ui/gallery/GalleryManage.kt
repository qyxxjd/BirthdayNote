@file:Suppress("unused")

package com.classic.birthday.ui.gallery

import androidx.fragment.app.FragmentActivity
import com.classic.core.ext.formatByte
import com.classic.core.ext.safeText
import com.classic.core.ext.splitComma
import com.classic.core.interfaces.SingleCallback
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import timber.log.Timber

/**
 * 相册管理
 *
 * @author LiuBin
 * @version 2022/6/28 16:22
 */
class GalleryManage private constructor() {
    companion object {
        private var INSTANCE: GalleryManage? = null
        @JvmStatic
        fun get(): GalleryManage = INSTANCE ?: GalleryManage().apply { INSTANCE = this }
    }
    private fun gallery(activity: FragmentActivity) =
        PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(AppImageEngine.create())
            .setCropEngine(ImageCropEngine())
            .setCompressEngine(ImageCompressEngine())
            .isGif(false)
            .setSkipCropMimeType(PictureMimeType.ofGIF())

    /**
     * 单张照片选择
     */
    fun single(activity: FragmentActivity, listener: SingleCallback<String>) {
        gallery(activity)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result.isNullOrEmpty() || result.size <= 0) return
                    Timber.d("单选图片：${result[0].desc()}")
                    listener.onSingleResult(result[0].findValidPath())
                }
                override fun onCancel() {}
            })
    }
}

fun LocalMedia.desc() = "$fileName $width x $height size:${size.formatByte()} \n path:$path \n realPath:$realPath \n compressPath:$compressPath \n cutPath:$cutPath "
fun LocalMedia.findValidPath(): String {
    if (!compressPath.isNullOrEmpty()) return compressPath
    if (!cutPath.isNullOrEmpty()) return cutPath
    if (!realPath.isNullOrEmpty()) return realPath
    return path.safeText()
}
fun String.toLocalMedia(): LocalMedia {
    return LocalMedia().also {
        it.path = this
        it.cutPath = this
        it.realPath = this
        it.compressPath = this
    }
}
fun String.toLocalMediaList(): List<LocalMedia> {
    val list = mutableListOf<LocalMedia>()
    this.splitComma().forEach { url ->
        list.add(url.toLocalMedia())
    }
    return list
}