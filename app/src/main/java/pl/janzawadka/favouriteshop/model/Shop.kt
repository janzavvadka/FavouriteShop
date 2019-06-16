package pl.janzawadka.favouriteshop.model

import com.google.firebase.firestore.GeoPoint

data class Shop(
    var uuid: String,
    var name: String = "",
    var description: String,
    var geopoint: GeoPoint = GeoPoint(0.0,0.0),
    var range: Double = 0.0,
    var photoPath: String = "",
    var category: String = ""
) {
    constructor() : this("", "", "", GeoPoint(0.0, 0.0), 0.0, "", "")

}
