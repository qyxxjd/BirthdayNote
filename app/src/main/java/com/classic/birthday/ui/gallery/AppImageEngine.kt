package com.classic.birthday.ui.gallery

import android.content.Context
import android.widget.ImageView
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.luck.picture.lib.R
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper

/**
 * 相册选择-图片加载实现类
 *
 * @author LiuBin
 * @version 2022/6/17 15:04
 */
class AppImageEngine private constructor() : ImageEngine {
    companion object {
        private var INSTANCE: AppImageEngine? = null
        fun create(): AppImageEngine = INSTANCE ?: AppImageEngine()
            .also { INSTANCE = it }
    }

    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        val target = ImageRequest.Builder(context)
            .data(url)
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun loadImage(
        context: Context?,
        imageView: ImageView?,
        url: String?,
        maxWidth: Int,
        maxHeight: Int
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        context?.let {
            val builder = ImageRequest.Builder(it)
            if (maxWidth > 0 && maxHeight > 0) {
                builder.size(maxWidth, maxHeight)
            }
            imageView?.let { v -> builder.data(url).target(v) }
            val request = builder.build()
            context.imageLoader.enqueue(request)
        }
    }

    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val target = ImageRequest.Builder(context)
            .data(url)
            .transformations(RoundedCornersTransformation(8F))
            .size(180, 180)
            .placeholder(R.drawable.ps_image_placeholder)
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val target = ImageRequest.Builder(context)
            .data(url)
            .size(270, 270)
            .placeholder(R.drawable.ps_image_placeholder)
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun pauseRequests(context: Context) {
    }
    override fun resumeRequests(context: Context) {
    }
}