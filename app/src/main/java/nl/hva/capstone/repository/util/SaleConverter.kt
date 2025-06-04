package nl.hva.capstone.repository.util

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import nl.hva.capstone.data.model.Sale

class SaleConverter {
    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Sale? {
            return try {
                val id = snapshot.getLong("id") ?: 0L
                val dateTime = snapshot.getTimestamp("dateTime") ?: Timestamp(0, 0)
                val clientName = snapshot.getString("clientName") ?: "Onbekend"
                val nextAppointmentDate = snapshot.getTimestamp("nextAppointmentDate")

                Sale(
                    id = id,
                    dateTime = dateTime,
                    clientName = clientName,
                    nextAppointmentDate = nextAppointmentDate
                )
            } catch (e: Exception) {
                Log.e("SaleConverter", "Error converting snapshot to Sale: ${e.message}")
                null
            }
        }
    }
}
