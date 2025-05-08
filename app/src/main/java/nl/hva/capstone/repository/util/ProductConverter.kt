package nl.hva.capstone.repository.util

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import nl.hva.capstone.data.model.Product

class ProductConverter {
    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Product? {
            return try {
                val id = snapshot.getLong("id") ?: 0L
                val name = snapshot.getString("name") ?: ""
                val salePrice = snapshot.getDouble("salePrice")
                val purchasePrice = snapshot.getDouble("purchasePrice")
                val taxes = snapshot.getLong("taxes")?.toInt()
                val imageUrl = snapshot.getString("image")
                val imageUri = imageUrl?.let { Uri.parse(it) }

                Product(
                    id = id,
                    name = name,
                    salePrice = salePrice,
                    purchasePrice = purchasePrice,
                    taxes = taxes,
                    image = imageUri
                )
            } catch (e: Exception) {
                Log.e("ProductConverter", "Error converting snapshot to Product: ${e.message}")
                null
            }
        }
    }
}
