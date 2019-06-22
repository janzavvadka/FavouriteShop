package pl.janzawadka.favouriteshop.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pl.janzawadka.favouriteshop.R
import pl.janzawadka.favouriteshop.database.DatabaseService
import pl.janzawadka.favouriteshop.permission.PermissionService

class MapOfShops : AppCompatActivity(), OnMapReadyCallback {
    private var mLocationPermissionsGranted: Boolean = false
    private var mMap: GoogleMap? = null
    private var rangeColor = "#2271cce7"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_of_shops)

        PermissionService.permissionForLocation(this)
        initMap()
    }

    fun initMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_shops) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@MapOfShops)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show()
        mMap = googleMap

        DatabaseService.findAllShopsForCurrentUser {
            it.forEach { shop ->
                val position = LatLng(shop.geopoint.latitude, shop.geopoint.longitude)
                mMap!!.addMarker(MarkerOptions().position(position).title(shop.name))
                mMap!!.addCircle(
                    CircleOptions()
                        .center(position)
                        .radius(shop.range)
                        .strokeColor(Color.BLUE)
                        .fillColor(Color.parseColor(rangeColor))
                        .strokeWidth(2f)
                )
            }
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
