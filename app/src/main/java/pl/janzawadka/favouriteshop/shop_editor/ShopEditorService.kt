package pl.janzawadka.favouriteshop.shop_editor

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import pl.janzawadka.favouriteshop.R
import pl.janzawadka.favouriteshop.database.DatabaseService
import pl.janzawadka.favouriteshop.database.StorageService
import pl.janzawadka.favouriteshop.model.Shop
import pl.janzawadka.favouriteshop.intent_operation.ShopOperation
import java.io.ByteArrayOutputStream
import java.util.*

class ShopEditorService(val activity: ShopEditorActivity) {
    var storage = FirebaseStorage.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100

    fun savePicture(operation: String) {
        validateFields()

        val imageReference: StorageReference? = storage.reference
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

        val uploadTask = imageReference!!.putBytes(data)

        activity.showSnackbar("Saving...")

        uploadTask.addOnFailureListener { exception ->
            Log.w("", exception.message)
        }.addOnSuccessListener {
            activity.selectShop!!.photoPath = imageReference.path
        }.addOnCompleteListener {
            saveShop(operation)
            activity.showList()
        }
    }


    fun saveShop(operation: String) {
        val nameField: TextView = getElementById(R.id.shop_name_field)
        val descField: TextView = getElementById(R.id.shop_description_field)
        val latitudeField: TextView = getElementById(R.id.shop_latitude_field)
        val longitudeField: TextView = getElementById(R.id.shop_longitude_field)
        val rangeField: TextView = getElementById(R.id.range_field)

        val shop = Shop()

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
            shop.uuid = activity.selectShop!!.uuid
            DatabaseService.updateShop(shop)
        } else {
            DatabaseService.addShop(shop)
        }
    }

    fun findImage() {
        val imageView: ImageView = getElementById(R.id.picture_th)

        if (activity.shopImage != null) {
            imageView.setImageBitmap(activity.shopImage)
        } else if(!activity.selectShop!!.photoPath.isBlank()){
            val pictureReference = StorageService.findPictureByLink(activity.selectShop!!.photoPath)

            PictureUtil.bitmapFromReference(pictureReference) {
                activity.shopImage = it
                imageView.setImageBitmap(it)
            }
        }
    }

    fun validateFields(): Boolean {
        val nameField: TextView = getElementById(R.id.shop_name_field)
        var errorMsg = ""

        if(nameField.text.isBlank()){
            errorMsg += "Name should contain something"
        }

        return if (!errorMsg.isBlank()) {
            activity.showSnackbar(errorMsg)
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
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
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


    fun getShopfromJson(json: String?): Shop {
        if(json.isNullOrBlank()){
            return Shop()
        }
        return Gson().fromJson(json, Shop::class.java)
    }

    fun getImagefromJson(json: String?): Bitmap? {
        if(json.isNullOrBlank()){
            return null
        }
        return Gson().fromJson(json, Bitmap::class.java)
    }

    fun <T : View> getElementById(id: Int): T {
        return activity.findViewById(id)
    }
}