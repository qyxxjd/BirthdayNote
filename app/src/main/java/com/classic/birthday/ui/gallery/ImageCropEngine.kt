package com.classic.birthday.ui.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.imageLoader
import coil.request.ImageRequest
import com.luck.picture.lib.engine.CropFileEngine
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine

/**
 * 图片裁剪
 *
 * @author LiuBin
 * @version 2022/6/17 15:27
 */
class ImageCropEngine : CropFileEngine {
    override fun onStartCrop(
        fragment: Fragment,
        srcUri: Uri,
        destinationUri: Uri,
        dataSource: ArrayList<String>,
        requestCode: Int
    ) {
        val options = UCrop.Options()
        val uCrop = UCrop.of(srcUri, destinationUri, dataSource)
        // options.setAspectRatioOptions(
        //     0,
        //     AspectRatio("裁剪比例4:3", 4f, 3f),
        //     AspectRatio("裁剪比例16:9", 16f, 9f)
        // )
        val accent = Color.parseColor("#20242F")
        options.setStatusBarColor(accent)
        options.setToolbarColor(accent)
        options.setToolbarWidgetColor(Color.WHITE)
        options.setToolbarTitle("图片裁剪")
        uCrop.withOptions(options)
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(context: Context, url: String, imageView: ImageView) {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                val target = ImageRequest.Builder(context)
                    .data(url)
                    .size(180, 180)
                    .placeholder(com.luck.picture.lib.R.drawable.ps_image_placeholder)
                    .target(imageView)
                    .build()
                context.imageLoader.enqueue(target)
            }

            override fun loadImage(
                context: Context,
                url: Uri,
                maxWidth: Int,
                maxHeight: Int,
                call: UCropImageEngine.OnCallbackListener<Bitmap>
            ) {
            }
        })
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
    }
}