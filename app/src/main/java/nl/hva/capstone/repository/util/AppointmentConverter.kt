package nl.hva.capstone.repository.util

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import nl.hva.capstone.data.model.Appointment

class AppointmentConverter {
    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Appointment? {
            return try {
                val id = snapshot.getLong("id") ?: 0L
                val clientId = snapshot.getLong("clientId") ?: 0L
                val serviceId = snapshot.getLong("serviceId") ?: 0L
                val dateTime = snapshot.getTimestamp("dateTime") ?: Timestamp.now()
                val description = snapshot.getString("description") ?: ""
                val notes = snapshot.getString("notes") ?: ""
                val checkedOut = snapshot.getBoolean("checkedOut") ?: false


                Appointment(
                    id = id,
                    clientId = clientId,
                    serviceId = serviceId,
                    dateTime = dateTime,
                    description = description,
                    notes = notes,
                    checkedOut = checkedOut
                )
            } catch (e: Exception) {
                Log.e("AppointmentConverter", "Error converting snapshot to Appointment: ${e.message}")
                null
            }
        }
    }
}
