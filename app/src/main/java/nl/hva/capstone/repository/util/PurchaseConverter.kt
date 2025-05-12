package nl.hva.capstone.repository.util

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import nl.hva.capstone.data.model.Purchase

class PurchaseConverter {
    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Purchase? {
            return try {
                val id = snapshot.getLong("id") ?: 0L
                val name = snapshot.getString("name") ?: ""
                val price = snapshot.getDouble("price")
                val taxes = snapshot.getLong("taxes")?.toInt()
                val dateTime = snapshot.getTimestamp("dateTime") ?: Timestamp(0, 0)
                val imageUrl = snapshot.getString("image")
                val imageUri = imageUrl?.let { Uri.parse(it) }

                Purchase(
                    id = id,
                    name = name,
                    price = price,
                    dateTime = dateTime,
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
