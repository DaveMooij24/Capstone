package nl.hva.capstone.repository

import androidx.compose.ui.text.toLowerCase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.Login
import java.security.MessageDigest
import java.util.Locale

class LoginRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val loginCollection = firestore.collection("logins")


    suspend fun checkCredentials(username: String, hashedPassword: String): Boolean {
        return try {
            val querySnapshot = loginCollection
                .whereEqualTo("username", username.lowercase(Locale.getDefault()))
                .whereEqualTo("password", hashedPassword)
                .get()
                .await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveLogin(login: Login) {
        loginCollection
            .document(login.username)
            .set(login)
            .await()
    }

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
