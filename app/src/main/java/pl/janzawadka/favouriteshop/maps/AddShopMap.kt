package pl.janzawadka.favouriteshop.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import pl.janzawadka.favouriteshop.R
import pl.janzawadka.favouriteshop.model.Shop
import pl.janzawadka.favouriteshop.shop_editor.ShopEditorActivity
import pl.janzawadka.favouriteshop.shop_editor.static.ShopOperation

class AddShopMap : AppCompatActivity(), OnMapReadyCallback {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234

    private var mLocationPermissionsGranted: Boolean? = false
    private var mMap: GoogleMap? = null

    private var selectShop: Shop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shop_map)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val confirmButton: Button = findViewById(R.id.confirm_button)

        selectShop = Gson().fromJson(intent.getStringExtra(ShopOperation.KEY_SHOP), Shop::class.java)

        confirmButton.setOnClickListener {

        }

        getLocationPermission()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var options = GoogleMapOptions()
        options.scrollGesturesEnabled(true)
        options.compassEnabled(true)

        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        fusedLocationClient.lastLocation.addOnSuccessListener {
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
        }
        mMap!!.setMinZoomPreference(20f)
        mMap!!.setMaxZoomPreference(15f)

        mMap!!.setOnMapClickListener {
           var latLng: LatLng = it
            mMap!!.addMarker(MarkerOptions().position(latLng).title(selectShop!!.name))
            selectShop!!.geopoint = GeoPoint(latLng.latitude, latLng.longitude)
            val intent = Intent(this, ShopEditorActivity::class.java)
            intent.putExtra(ShopOperation.KEY_OPERATION, ShopOperation.EDIT)
            intent.putExtra(ShopOperation.KEY_SHOP, Gson().toJson(selectShop))
            startActivity(intent)
        }


        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment!!.getMapAsync(this@AddShopMap)
    }

    private fun getLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (ContextCompat.checkSelfPermission(this.applicationContext,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
        if (ContextCompat.checkSelfPermission(this.applicationContext,  Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }

        initMap()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mLocationPermissionsGranted = false

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    for (i in grantResults.indices) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false
                            Log.d("", "onRequestPermissionsResult: permission failed")
                            return
                        }
                    }
                    Log.d("", "onRequestPermissionsResult: permission granted")
                    mLocationPermissionsGranted = true
                    initMap()
                }
            }
        }
    }
}
