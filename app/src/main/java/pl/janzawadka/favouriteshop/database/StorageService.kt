package pl.janzawadka.favouriteshop.database

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object StorageService {

    fun findPictureByLink(path: String): StorageReference{
        val storage = FirebaseStorage.getInstance()
        return storage.reference.child(path)
    }
}