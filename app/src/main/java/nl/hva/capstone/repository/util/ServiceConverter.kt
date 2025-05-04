package nl.hva.capstone.repository.util

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import nl.hva.capstone.data.model.Service

class ServiceConverter {
    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Service? {
            return try {
                val id = snapshot.getLong("id") ?: 0L
                val name = snapshot.getString("name") ?: ""
                val estimatedTimeMinutes = snapshot.getLong("estimatedTimeMinutes")?.toInt() ?: 0
                val price = snapshot.getDouble("price") ?: 0.0
                val taxes = snapshot.getLong("taxes")?.toInt() ?: 0

                Service(
                    id = id,
                    name = name,
                    estimatedTimeMinutes = estimatedTimeMinutes,
                    price = price,
                    taxes = taxes
                )
            } catch (e: Exception) {
                Log.e("ServiceConverter", "Error converting snapshot to Service: ${e.message}")
                null
            }
        }
    }
}
