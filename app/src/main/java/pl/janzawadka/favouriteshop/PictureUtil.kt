package pl.janzawadka.favouriteshop

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.storage.StorageReference
import pl.janzawadka.favouriteshop.model.Shop

object PictureUtil {

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
        rawPicture.getBytes(8_000_000).addOnSuccessListener {
            val image = BitmapFactory.decodeByteArray(it, 0, it.size)
            consumer(image)
        }.addOnFailureListener {exception ->
            Log.d("", exception.toString())
        }
    }

}