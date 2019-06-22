package pl.janzawadka.favouriteshop.shop_editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.util.Log
import com.google.firebase.storage.StorageReference

object PictureUtil {

    const val MAX_PHOTO_BYTE = 8_000_000L

    fun convertVectorToBitmap(drawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun bitmapFromReference(rawPicture: StorageReference, consumer: (Bitmap) -> Unit){
        rawPicture.getBytes(MAX_PHOTO_BYTE).addOnSuccessListener {
            val image = BitmapFactory.decodeByteArray(it, 0, it.size)
            consumer(image)
        }.addOnFailureListener {exception ->
            Log.w("", exception.toString())
        }
    }

}