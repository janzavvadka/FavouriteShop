package pl.janzawadka.favouriteshop.database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pl.janzawadka.favouriteshop.model.Shop
import java.util.*
import kotlin.collections.ArrayList

object DatabaseService {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private const val shopCollection = "shop"
    private const val userShopCollection = "user_shop"

    fun findAllShopsForCurrentUser(consumer: (ArrayList<Shop>) -> Unit) {
        val database = FirebaseFirestore.getInstance()
        val shops: ArrayList<Shop> = ArrayList()

        database.collection(shopCollection)
            .document("${userId}")
            .collection(userShopCollection)
            .get()
            .addOnSuccessListener { result ->
                result.forEach {
                    val shop: Shop = it.toObject(Shop::class.java)
                    shops.add(shop)
                    Log.d("", "Getting all shops success")
                }
                consumer(shops)
            }
            .addOnFailureListener { exception ->
                Log.d("", "Error getting documents: ", exception)
            }
    }

    fun updateShop(shop: Shop) {
        val database = FirebaseFirestore.getInstance()
        database.collection(shopCollection)
            .document("${userId}")
            .collection(userShopCollection)
            .document(shop.uuid)
            .set(shop)
            .addOnSuccessListener {
                Log.d("", "Shop successfully updated")
            }
            .addOnFailureListener {
                Log.w("", "Error while updating shop", it)
            }
    }

    fun addShop(shop: Shop) {
        val database = FirebaseFirestore.getInstance()

        val uuid: String = UUID.randomUUID().toString()
        shop.uuid = uuid

        database.collection(shopCollection)
            .document("${userId}")
            .collection(userShopCollection)
            .document(uuid)
            .set(shop)
            .addOnSuccessListener {
                Log.d("", "Shop successfully add")
            }
            .addOnFailureListener {
                Log.w("", "Error adding shop", it)
            }
    }

    fun removeShop(uuid: String) {
        val database = FirebaseFirestore.getInstance()

        database.collection(shopCollection)
            .document("${userId}")
            .collection(userShopCollection)
            .document(uuid)
            .delete()
            .addOnSuccessListener {
                Log.d("", "Shop successfully deleted")
            }
            .addOnFailureListener {
                Log.w("", "Error while deleting shop", it)
            }
    }
}
