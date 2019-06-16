package pl.janzawadka.favouriteshop.database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pl.janzawadka.favouriteshop.model.Shop
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import kotlin.collections.ArrayList

object DatabaseService {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val shopCollection = "shop"
    private val userShopcollection = "user_shop"

    fun findAllShopsForCurrentUser(consumer: (ArrayList<Shop>) -> Unit) {
        val database = FirebaseFirestore.getInstance()
        val shops: ArrayList<Shop>  = ArrayList()

        database.collection(shopCollection)
            .document("${userId}")
            .collection(userShopcollection)
            .get()
            .addOnSuccessListener { result ->
                result.forEach {
                    val shopDTO: Shop = it.toObject(Shop::class.java)
                    shops.add(shopDTO)
                    Log.d("", "Getting all shops success")
                    consumer(shops)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {


            }
    }

    fun updateShop(shop: Shop) {
        val database = FirebaseFirestore.getInstance()
        database.collection(shopCollection)
            .document("${userId}")
            .collection(userShopcollection)
            .document(shop.uuid)
            .set(shop)
            .addOnSuccessListener {
                Log.d("", "Shop successfully updated")
            }
            .addOnFailureListener {
                Log.d("", "Error while updating shop")
            }
    }

    fun addShop(shop: Shop) {
        val database = FirebaseFirestore.getInstance()

        val uuid: String = UUID.randomUUID().toString()
        shop.uuid = uuid

        database.collection(shopCollection)
            .document("${userId}")
            .collection(userShopcollection)
            .document(uuid)
            .set(shop)
            .addOnSuccessListener {
                Log.d("", "DocumentSnapshot written with ID: $uuid")
            }
            .addOnFailureListener { e ->
                Log.w("", "Error adding document", e)
            }
    }

    fun removeShop(shop: Shop) {
        val database = FirebaseFirestore.getInstance()

        database.collection(shopCollection)
            .document("${userId}")
            .collection(userShopcollection)
            .document(shop.uuid)
            .delete()
            .addOnSuccessListener {
                Log.d("", "Shop successfully deleted")
            }
            .addOnFailureListener {
                Log.d("", "Error while deleting shop")
            }
    }
}
