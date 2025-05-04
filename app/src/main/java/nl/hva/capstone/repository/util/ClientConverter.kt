package nl.hva.capstone.repository.util

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import nl.hva.capstone.data.model.Client

class ClientConverter {
    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Client? {
            return try {
                val id = snapshot.getLong("id") ?: 0L
                val name = snapshot.getString("name") ?: ""
                val gender = snapshot.getString("gender") ?: ""
                val phone = snapshot.getString("phone") ?: ""
                val email = snapshot.getString("email") ?: ""
                val color = snapshot.getString("color") ?: ""
                val notes = snapshot.getString("notes") ?: ""

                Client(
                    id = id,
                    name = name,
                    gender = gender,
                    phone = phone,
                    email = email,
                    color = color,
                    notes = notes
                )
            } catch (e: Exception) {
                Log.e("ClientConverter", "Error converting snapshot to Client: ${e.message}")
                null
            }
        }
    }
}