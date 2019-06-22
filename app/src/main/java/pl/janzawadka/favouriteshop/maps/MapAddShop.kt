package pl.janzawadka.favouriteshop.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import pl.janzawadka.favouriteshop.R
import pl.janzawadka.favouriteshop.model.Shop
import pl.janzawadka.favouriteshop.shop_editor.ShopEditorActivity
import pl.janzawadka.favouriteshop.intent_operation.ShopOperation
import pl.janzawadka.favouriteshop.permission.PermissionService

class MapAddShop : AppCompatActivity(), OnMapReadyCallback {

    private var mLocationPermissionsGranted: Boolean = false
    private var mMap: GoogleMap? = null

    private var selectShop: Shop? = null
    private var image: Bitmap? = null
    private var operation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_add_shop)

        selectShop = Gson().fromJson(intent.getStringExtra(ShopOperation.KEY_SHOP), Shop::class.java)
        image = Gson().fromJson(intent.getStringExtra(ShopOperation.KEY_IMAGE), Bitmap::class.java)
        operation = Gson().fromJson(intent.getStringExtra(ShopOperation.KEY_OPERATION), String::class.java)

        PermissionService.permissionForLocation(this)
        initMap()
    }

    fun initMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_add) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@MapAddShop)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap!!.setOnMapClickListener {
            val latLng: LatLng = it
            selectShop!!.geopoint = GeoPoint(latLng.latitude, latLng.longitude)
            val intent = Intent(this, ShopEditorActivity::class.java)
            intent.putExtra(ShopOperation.KEY_MODIFY, ShopOperation.MODIFY)
            intent.putExtra(ShopOperation.KEY_OPERATION, operation)
            intent.putExtra(ShopOperation.KEY_SHOP, Gson().toJson(selectShop))
            intent.putExtra(ShopOperation.KEY_IMAGE, Gson().toJson(image))
            startActivity(intent)
        }


        PermissionService.permissionForLocation(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        moveCameraToYourLocation()

    }

    @SuppressLint("MissingPermission")
    private fun moveCameraToYourLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 11f))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mLocationPermissionsGranted = false

        if (requestCode == PermissionService.PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty()) {
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false
                        Log.w("", "Localization: permission failed")
                        return
                    }
                }
                Log.d("", "Localization: permission granted")
                mLocationPermissionsGranted = true
                initMap()
            }
        }
    }
}
