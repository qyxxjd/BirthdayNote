package com.classic.core.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ImageSpan
import android.text.style.ReplacementSpan

/**
 * 居中显示的ImageSpan
 */
class CenterImageSpan(context: Context, bitmap: Int) : ImageSpan(context, bitmap) {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        fm?.let {
            val fontHeight = paint.fontMetricsInt.descent - paint.fontMetricsInt.ascent
            val imageHeight = drawable.bounds.height()
            it.ascent = paint.fontMetricsInt.ascent - ((imageHeight - fontHeight) / 2.0f).toInt()
            it.top = it.ascent
            it.descent = it.ascent + imageHeight
            it.bottom = it.descent
        }
        return drawable.bounds.right
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,//line top,It is the largest top in line.
        y: Int,//baseline y position
        bottom: Int,//line bottom,contain lineSpacingExtra if there is more than one line.
        paint: Paint
    ) {
        val fontHeight = paint.fontMetricsInt.descent - paint.fontMetricsInt.ascent
        val imageAscent = paint.fontMetricsInt.ascent - ((drawable.bounds.height() - fontHeight) / 2.0f).toInt()
        canvas.save()
        canvas.translate(x, (y + imageAscent).toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}

/**
 * 圆角背景
 *
 * @param backgroundColor 背景颜色
 * @param radius 圆角
 */
class RadiusBackgroundSpan(
    private val textColor: Int,
    private val backgroundColor: Int,
    private val radius: Float
) : ReplacementSpan() {
    private var size: Int = 0
    override fun getSize(
        paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        size = (paint.measureText(text, start, end) + 2 * radius).toInt()
        return size
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        if (text.isNullOrEmpty()) return
        // 设置背景颜色
        paint.color = backgroundColor
        // 设置画笔的锯齿效果
        paint.isAntiAlias = true
        val oval = RectF(x, y + paint.ascent(), x + size, y + paint.descent())
        // 设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        // 绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        canvas.drawRoundRect(oval, radius, radius, paint)
        // 设置画笔的文字颜色
        paint.color = textColor
        // 绘制文字
        canvas.drawText(text, start, end, x + radius, y.toFloat(), paint)
    }
}