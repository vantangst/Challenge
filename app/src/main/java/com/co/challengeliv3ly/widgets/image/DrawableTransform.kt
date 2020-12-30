package com.co.challengeliv3ly.widgets.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Build.ID
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.concurrent.locks.ReentrantLock

abstract class DrawableTransform(private val roundingRadius: Int) : BitmapTransformation() {
    companion object {
        private val BITMAP_DRAWABLE_LOCK = ReentrantLock()
    }

    protected abstract val id: String
    private val idBytes = ID.toByteArray(CHARSET)

    override fun transform(pool: BitmapPool, inBitmap: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val safeConfig = getAlphaSafeConfig(inBitmap)
        val toTransform = getAlphaSafeBitmap(pool, inBitmap)
        val result = pool.get(toTransform.width, toTransform.height, safeConfig)

        result.setHasAlpha(true)
        BITMAP_DRAWABLE_LOCK.lock()
        try {
            val canvas = Canvas(result)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            onDraw(canvas, toTransform)
            canvas.setBitmap(null)
        } finally {
            BITMAP_DRAWABLE_LOCK.unlock()
        }

        if (toTransform != inBitmap) {
            pool.put(toTransform)
        }

        return result
    }

    protected abstract fun onDraw(canvas: Canvas, toTransform: Bitmap)

    private fun getAlphaSafeConfig(inBitmap: Bitmap): Bitmap.Config {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Bitmap.Config.RGBA_F16 == inBitmap.config) {
                return Bitmap.Config.RGBA_F16
            }
        }
        return Bitmap.Config.ARGB_8888
    }

    private fun getAlphaSafeBitmap(pool: BitmapPool, maybeAlphaSafe: Bitmap): Bitmap {
        val safeConfig = getAlphaSafeConfig(maybeAlphaSafe)
        if (safeConfig == maybeAlphaSafe.config) {
            return maybeAlphaSafe
        }
        val argbBitmap = pool.get(maybeAlphaSafe.width, maybeAlphaSafe.height, safeConfig)
        Canvas(argbBitmap).drawBitmap(maybeAlphaSafe, 0f /*left*/, 0f /*top*/, null /*paint*/)
        return argbBitmap
    }

    override fun equals(other: Any?): Boolean {
        if (other is DrawableTransform) {
            return roundingRadius == other.roundingRadius
        }
        return false
    }

    override fun hashCode(): Int {
        return Util.hashCode(ID.hashCode(),
            Util.hashCode(roundingRadius))
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(idBytes)

        val radiusData = ByteBuffer.allocate(4).putInt(roundingRadius).array()
        messageDigest.update(radiusData)
    }

}