package com.zubair.jarvis

import android.content.Context
import android.graphics.Canvas
import android.graphics.Movie
import android.graphics.Paint
import android.os.SystemClock
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/** Lightweight animated GIF surface rendered behind the Compose interface. */
@Composable
internal fun AnimatedJarvisBackground(modifier: Modifier = Modifier) {
    AndroidView(modifier = modifier, factory = { context -> JarvisGifView(context) })
}

private class JarvisGifView(context: Context) : View(context) {
    private val movie: Movie? = runCatching {
        context.resources.openRawResource(R.raw.jarvis_core).use { Movie.decodeStream(it) }
    }.getOrNull()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val animation = movie ?: return
        val duration = animation.duration().coerceAtLeast(1)
        animation.setTime((SystemClock.uptimeMillis() % duration).toInt())
        val sourceWidth = animation.width().toFloat().coerceAtLeast(1f)
        val sourceHeight = animation.height().toFloat().coerceAtLeast(1f)
        val scale = maxOf(width / sourceWidth, height / sourceHeight)
        val left = (width - sourceWidth * scale) / 2f
        val top = (height - sourceHeight * scale) / 2f
        canvas.save()
        canvas.translate(left, top)
        canvas.scale(scale, scale)
        animation.draw(canvas, 0f, 0f, paint)
        canvas.restore()
        if (isAttachedToWindow) postInvalidateDelayed(40L)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        invalidate()
    }
}
