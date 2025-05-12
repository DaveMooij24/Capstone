package nl.hva.capstone.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.Purchase
import nl.hva.capstone.repository.util.PurchaseConverter
import java.util.*

class PurchaseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val purchaseCollection = firestore.collection("purchases")

    suspend fun savePurchaseWithImage(purchase: Purchase): Purchase {
        val imageUri = purchase.image
        val imageUrl = imageUri?.let { uploadImageToFirebase(it) }

        val purchaseToSave = purchase.copy(image = imageUrl?.let { Uri.parse(it) })

        val dataMap = mapOf(
            "id" to purchaseToSave.id,
            "name" to purchaseToSave.name,
            "price" to purchaseToSave.price,
            "taxes" to purchaseToSave.taxes,
            "image" to purchaseToSave.image?.toString(),
            "dateTime" to purchaseToSave.dateTime
        )

        purchaseCollection
            .document(purchaseToSave.id.toString())
            .set(dataMap)
            .await()

        return purchaseToSave
    }

    suspend fun savePurchase(purchase: Purchase) {
        val dataMap = mapOf(
            "id" to purchase.id,
            "name" to purchase.name,
            "price" to purchase.price,
            "taxes" to purchase.taxes,
            "image" to purchase.image?.toString(),
            "dateTime" to purchase.dateTime
        )

        purchaseCollection
            .document(purchase.id.toString())
            .set(dataMap)
            .await()
    }

    suspend fun getPurchaseById(id: Long): Purchase? {
        val document = purchaseCollection.document(id.toString()).get().await()
        return PurchaseConverter.fromSnapshot(document)
    }

    suspend fun getPurchases(): List<Purchase> {
        val snapshot = purchaseCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            PurchaseConverter.fromSnapshot(doc)
        }
    }

    private suspend fun uploadImageToFirebase(uri: Uri): String {
        val fileName = "purchase_images/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
