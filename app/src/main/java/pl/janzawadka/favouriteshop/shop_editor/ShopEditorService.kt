package pl.janzawadka.favouriteshop.shop_editor

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import pl.janzawadka.favouriteshop.PictureUtil
import pl.janzawadka.favouriteshop.R
import pl.janzawadka.favouriteshop.database.DatabaseService
import pl.janzawadka.favouriteshop.database.StorageService
import pl.janzawadka.favouriteshop.model.Shop
import pl.janzawadka.favouriteshop.shop_editor.static.ShopOperation
import java.io.ByteArrayOutputStream
import java.util.*

class ShopEditorService(val activity: ShopEditorActivity) {
    var storage = FirebaseStorage.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100

    fun savePicture(operation: String) {
        validateFields()

        val imageRef: StorageReference? = storage.reference
            .child("$userId")
            .child(activity.selectShop!!.uuid + ".jpg")

        val imageView: ImageView = getElementById(R.id.picture_th)

        val bitmap = try {
            (imageView.drawable as BitmapDrawable).bitmap
        } catch (e: ClassCastException) {
            PictureUtil.convertVectorToBitmap(imageView.drawable as VectorDrawable)
        }

        val pictureBytes = ByteArrayOutputStream()

        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, pictureBytes)
        val data = pictureBytes.toByteArray()

        val uploadTask = imageRef!!.putBytes(data)

        activity.showSnackbar("Saving...")

        uploadTask.addOnFailureListener { exception ->
            Log.d("UPLOAD TASK", exception.message)
        }.addOnSuccessListener {
            activity.selectShop!!.photoPath = imageRef.path
        }.addOnCompleteListener {
            saveShop(operation)
            activity.showList()
        }
    }


    fun saveShop(operation: String) {
        val idField: TextView = getElementById(R.id.shop_id)
        val nameField: TextView = getElementById(R.id.shop_name_field)
        val descField: TextView = getElementById(R.id.shop_description_field)
        val latitudeField: TextView = getElementById(R.id.shop_latitude_field)
        val longitudeField: TextView = getElementById(R.id.shop_longitude_field)
        val rangeField: TextView = getElementById(R.id.range_field)

        val shop = Shop()

        if (operation != ShopOperation.EDIT) {
            shop.uuid = UUID.randomUUID().toString()
        } else {
            shop.uuid = idField.text.toString()
        }

        val latitude: Double = (latitudeField.text.toString()).toDouble()
        val longitude: Double = (longitudeField.text.toString()).toDouble()
        val geopoint = GeoPoint(latitude, longitude)

        shop.name = nameField.text.toString()
        shop.description = descField.text.toString()
        shop.geopoint = geopoint
        shop.range = (rangeField.text.toString()).toDouble()
        shop.photoPath = activity.selectShop!!.photoPath
        shop.category = activity.selectShop!!.category

        if (operation == ShopOperation.EDIT) {
            DatabaseService.updateShop(shop)
        } else {
            DatabaseService.addShop(shop)
        }
    }

    fun findImage() {
        val imageView: ImageView = getElementById(R.id.picture_th)

        if (activity.shopImage != null) {
            imageView.setImageBitmap(activity.shopImage)
        } else {
            val pictureReference = StorageService.findPictureByLink(activity.selectShop!!.photoPath)

            PictureUtil.bitmapFromReference(pictureReference) {
                imageView.setImageBitmap(it)
            }
        }
    }

    fun validateFields(): Boolean {
        val rangeField: TextView = getElementById(R.id.range_field)

        return if (rangeField.text.toString().toDouble() == 0.0) {
            activity.showSnackbar("Range must be greater than zero!")
            false
        } else {
            true
        }
    }

    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }

        val latitude: TextView = getElementById(R.id.shop_latitude_field)
        val longitude: TextView = getElementById(R.id.shop_longitude_field)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        fusedLocationClient.lastLocation.addOnSuccessListener {
            latitude.text = it.latitude.toString()
            longitude.text = it.longitude.toString()
        }
    }

    fun getShopDTOfromJson(json: String): Shop {
        return Gson().fromJson(json, Shop::class.java)
    }


    fun <T : View> getElementById(id: Int): T {
        return activity.findViewById(id)
    }
}