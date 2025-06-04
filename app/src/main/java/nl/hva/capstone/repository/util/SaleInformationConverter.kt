package nl.hva.capstone.repository.util

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import nl.hva.capstone.data.model.SaleInformation

class SaleInformationConverter {
    companion object {
        fun fromSnapshot(saleId: Long, snapshot: DocumentSnapshot): SaleInformation? {
            return try {
                val name = snapshot.getString("name") ?: ""
                val price = snapshot.getDouble("price") ?: 0.0
                val tax = snapshot.getLong("tax")?.toInt() ?: 0
                val id = snapshot.get("id")?.toString() ?: ""

                SaleInformation(
                    saleId = saleId,
                    name = name,
                    price = price,
                    tax = tax,
                    id = id
                )
            } catch (e: Exception) {
                Log.e("SaleInfoConverter", "Error converting snapshot to SaleInformation: ${e.message}")
                null
            }
        }
    }
}
