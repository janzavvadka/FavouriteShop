package pl.janzawadka.favouriteshop.shop_editor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import pl.janzawadka.favouriteshop.shop_list.ShopListActivity
import pl.janzawadka.favouriteshop.R
import pl.janzawadka.favouriteshop.database.DatabaseService
import pl.janzawadka.favouriteshop.maps.AddShopMap
import pl.janzawadka.favouriteshop.model.Shop
import pl.janzawadka.favouriteshop.shop_editor.static.ShopOperation

class ShopEditorActivity : AppCompatActivity() {

    var selectShop: Shop? = null
    var shopImage: Bitmap? = null

    val shopEditorService: ShopEditorService = ShopEditorService(this)
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_editor)
        createListeners()
    }

    fun createListeners() {
        val saveButton: Button = findViewById(R.id.save_shop_button)
        val deleteShop: Button = findViewById(R.id.delete_shop)
        val shopMapButton: Button = findViewById(R.id.current_postion_button)
        val takePictureButton: Button = findViewById(R.id.picture_take)
        val operation: String = intent.getStringExtra(ShopOperation.KEY_OPERATION)


        setupSpinnerDropdownList()
        addListenerOnSpinnerItemSelection()

        if (selectShop == null)
            if (operation == ShopOperation.EDIT) {
                setupFields()
                shopEditorService.findImage()
            } else if (operation == ShopOperation.ADD) {
                selectShop = Shop()
            }

        saveButton.setOnClickListener {
            if (shopEditorService.validateFields()) {
                shopEditorService.savePicture(operation)
            }
        }

        val backButton: Button = findViewById(R.id.back_shop_button)
        backButton.setOnClickListener {
            intent = Intent(this, ShopListActivity::class.java)
            startActivity(intent)
        }

        deleteShop.setOnClickListener {
            if (!selectShop!!.uuid.isBlank()) {
                DatabaseService.removeShop(selectShop!!.uuid)
                intent = Intent(this, ShopListActivity::class.java)
                startActivity(intent)
            }
        }

        takePictureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        val getLocationButton: Button = findViewById(R.id.current_postion_button)

        getLocationButton.setOnClickListener {
            shopEditorService.getCurrentLocation()
        }

        shopMapButton.setOnClickListener {
            intent = Intent(this, AddShopMap::class.java)
            saveShopState()
            intent.putExtra(ShopOperation.KEY_SHOP, Gson().toJson(selectShop))
            startActivity(intent)
        }
    }

    private fun saveShopState() {
        if (selectShop == null) {
            selectShop = Shop()
        }
        val idField: TextView = findViewById(R.id.shop_id)
        val nameField: TextView = findViewById(R.id.shop_name_field)
        val descField: TextView = findViewById(R.id.shop_description_field)
        val latitudeField: TextView = findViewById(R.id.shop_latitude_field)
        val longitudeField: TextView = findViewById(R.id.shop_longitude_field)
        val rangeField: TextView = findViewById(R.id.range_field)

        selectShop!!.uuid = idField.text.toString()
        selectShop!!.name = nameField.text.toString()
        selectShop!!.description = descField.text.toString()
        selectShop!!.photoPath =

        val latitude: Double = (latitudeField.text.toString()).toDouble()
        val longitude: Double = (longitudeField.text.toString()).toDouble()
        val geopoint = GeoPoint(latitude, longitude)

        selectShop!!.geopoint = geopoint
        selectShop!!.range = (rangeField.text.toString()).toDouble()
    }

    fun setupFields() {
        val json: String = intent.getStringExtra(ShopOperation.KEY_SHOP)
        selectShop = shopEditorService.getShopDTOfromJson(json)

        val idField: TextView = findViewById(R.id.shop_id)
        val nameField: TextView = findViewById(R.id.shop_name_field)
        val descField: TextView = findViewById(R.id.shop_description_field)
        val latitude: TextView = findViewById(R.id.shop_latitude_field)
        val longitude: TextView = findViewById(R.id.shop_longitude_field)
        val rangeField: TextView = findViewById(R.id.range_field)

        idField.text = selectShop!!.uuid
        nameField.text = selectShop!!.name
        descField.text = selectShop!!.description
        latitude.text = selectShop!!.geopoint.latitude.toString()
        longitude.text = selectShop!!.geopoint.longitude.toString()
        rangeField.text = selectShop!!.range.toString()
    }

    fun setupSpinnerDropdownList() {
        val spinner: Spinner = findViewById(R.id.spinner_cat)
        ArrayAdapter.createFromResource(this, R.array.shops_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
    }

    fun showList() {
        intent = Intent(this, ShopListActivity::class.java)
        startActivity(intent)
    }

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imageView: ImageView = findViewById(R.id.picture_th)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            shopImage = imageBitmap
        }
    }



    fun showSnackbar(text: String) {
        val container = findViewById<View>(android.R.id.content)
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show()
        }
    }

    fun addListenerOnSpinnerItemSelection() {
        val spinner: Spinner = findViewById(R.id.spinner_cat)
        spinner.onItemSelectedListener = SpinnerActivity()
    }

    inner class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            if (parent.selectedItem != null) {
                selectShop!!.category = parent.selectedItem.toString()
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {

        }
    }
}
