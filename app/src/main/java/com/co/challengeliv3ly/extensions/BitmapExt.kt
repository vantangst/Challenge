package com.co.challengeliv3ly.extensions

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.io.*


fun Bitmap.scaleAndRecycleIfNeeded(
    maxWidth: Int = AppConst.MAX_WIDTH_SIZE,
    maxHeight: Int = AppConst.MAX_HEIGHT_SIZE
): Bitmap {
    val bitmapResize: Bitmap
    bitmapResize = if (width > maxWidth || height > maxHeight) {
        val ratio = if (width > maxWidth) width.toFloat() / maxWidth.toFloat()
        else height.toFloat() / maxHeight.toFloat()
        val newBmp = Bitmap.createScaledBitmap(
            this,
            (width / ratio).toInt(),
            (height / ratio).toInt(), false
        )
        recycle()
        newBmp
    } else this
    return bitmapResize
}

fun Bitmap.scale(maxWidth: Int = AppConst.MAX_WIDTH_SIZE, maxHeight: Int = AppConst.MAX_HEIGHT_SIZE): Bitmap {
    val bitmapResize: Bitmap
    bitmapResize = if (width > maxWidth || height > maxHeight) {
        val ratio = if (width > maxWidth) width.toFloat() / maxWidth.toFloat()
        else height.toFloat() / maxHeight.toFloat()
        val newBmp = Bitmap.createScaledBitmap(
            this,
            (width / ratio).toInt(),
            (height / ratio).toInt(), false
        )
        newBmp
    } else this
    return bitmapResize
}

fun Bitmap.scaleAndRecycle(size: Int): Bitmap {
    val bmp = scale(size)
    if (bmp != this) recycle()
    return bmp
}

fun Bitmap.scale(size: Int): Bitmap {
    val bitmapResize: Bitmap
    bitmapResize = if (width != size || height != size) {
        val smallest = width.coerceAtMost(height).toFloat()
        val factor = smallest / size
        Bitmap.createScaledBitmap(
            this,
            (width / factor).toInt(),
            (height / factor).toInt(), false
        )
    } else this
    return bitmapResize
}

fun Bitmap.saveToFileAndRecycle(
    context: Context,
    fileName: String?,
    ext: String = "jpg",
    quality: Int = 90
): String {
    val tmpName = if (!fileName.isNullOrEmpty()) fileName else System.currentTimeMillis().toString()
    val file = "${context.cacheDir}/$tmpName.$ext"

    FileOutputStream(file).use { out -> compress(Bitmap.CompressFormat.JPEG, quality, out) }
    recycle()
    return file
}

fun Bitmap.saveToVideoThumbnail(context: Context, quality: Int = 90): String {
    val folder = AppConst.getAppVideoThumbnailFolder(context)
    File(folder).mkdirs()
    val path = "$folder/${System.currentTimeMillis()}.jpg"
    FileOutputStream(path).use { out -> compress(Bitmap.CompressFormat.JPEG, quality, out) }
    recycle()
    return path
}

fun Bitmap.toBytes(): ByteArray {
    val bitmap = Bitmap.createBitmap(this)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
    val byteArray = stream.toByteArray()
    bitmap.recycle()
    return byteArray
}

fun Bitmap.cropCircle(size: Int): Bitmap {
    val bmCropped = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    Canvas(bmCropped).drawBitmapCircle(this, size / 2)
    return bmCropped
}

fun Bitmap.cropCircleAndRecycle(size: Int): Bitmap {
    val bmp = cropCircle(size)
    recycle()
    return bmp
}

fun Bitmap.doHighlightImage(shadowWidth: Int = 20): Bitmap {
    val bmOut = Bitmap.createBitmap(width + shadowWidth * 2, height + shadowWidth * 2, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmOut)
    canvas.drawColor(0, PorterDuff.Mode.CLEAR)
    val ptBlur = Paint()
    ptBlur.maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.OUTER)
    val offsetXY = IntArray(2)
    val bmAlpha = extractAlpha(ptBlur, offsetXY)
    val ptAlphaColor = Paint()
    ptAlphaColor.color = Color.BLACK
    canvas.drawBitmap(bmAlpha, offsetXY[0].toFloat(), offsetXY[1].toFloat(), ptAlphaColor)
    bmAlpha.recycle()
    canvas.drawBitmap(this, 0f, 0f, null)
    return bmOut
}

fun String.scalePhoto(context: Context, index: Int): String {
    val path = String.format("%s/" + index + "_photo.jpg", getTemplateFolder(context))
    var bitmap = rotateImage(context, this)
    bitmap = resize(bitmap, 400)
    saveBitmap(bitmap, path, 70)
    bitmap.recycle()
    return path
}

fun Uri.scalePhoto(context: Context, index: Int): String {
    val path = String.format("%s/" + index + "_photo.jpg", getTemplateFolder(context))
    var bitmap = rotateImage(context, this)
    bitmap = resize(bitmap, 400)
    saveBitmap(bitmap, path, 70)
    bitmap.recycle()
    return path
}

fun getTemplateFolder(context: Context): String {
    val folder = String.format("%s/temp", context.cacheDir)
    File(folder).mkdirs()
    return String.format("%s/temp", context.cacheDir)
}

fun resize(bitmap: Bitmap, maxSize: Int): Bitmap {
    var bitmapResize = bitmap
    if (bitmap.width != maxSize || bitmap.height != maxSize) {
        val smallest = Math.min(bitmap.width, bitmap.height).toFloat()
        val factor = smallest / maxSize
        val width = bitmap.width / factor
        val height = bitmap.height / factor
        bitmapResize = Bitmap.createScaledBitmap(
            bitmap,
            width.toInt(),
            height.toInt(), false
        )
        bitmap.recycle()
    }
    return bitmapResize
}

fun rotateImage(context: Context, pathFile: String): Bitmap {
    var myBitmap = BitmapFactory.decodeFile(pathFile)
    try {
        myBitmap = rotateImageIfRequired(myBitmap, pathFile)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return myBitmap
}

fun rotateImage(context: Context, uri: Uri): Bitmap {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

    var myBitmap = BitmapFactory.decodeStream(inputStream)
    //val bmOptions = BitmapFactory.Options()
    //var myBitmap = BitmapFactory.decodeFile(uri, bmOptions)
    try {
        myBitmap = rotateImageIfRequired(context, myBitmap, uri)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return myBitmap
}


@Throws(IOException::class)
fun rotateImageIfRequired(img: Bitmap, path: String): Bitmap {

    val ei = ExifInterface(path)
    val orientation =
        ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(img, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(img, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(img, 270)
        else -> img
    }
}

@Throws(IOException::class)
fun rotateImageIfRequired(context: Context, img: Bitmap, uri: Uri): Bitmap {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val ei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        ExifInterface(inputStream)
    } else {
        ExifInterface(uri.path)
    }
    val orientation =
        ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(img, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(img, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(img, 270)
        else -> img
    }
}

private fun saveBitmap(bitmap: Bitmap, path: String, quality: Int) {
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(File(path))
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IllegalStateException) {
        e.printStackTrace()
    }
}