package nl.hva.capstone.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.AppointmentProduct
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.repository.util.ProductConverter

class AppointmentProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionAppointmentProduct = firestore.collection("appointment_products")

    private val collectionProduct = firestore.collection("products")


    suspend fun addAppointmentProduct(appointmentProduct: AppointmentProduct) {
        val docRef = collectionAppointmentProduct.document()
        val newProduct = appointmentProduct.copy(id = docRef.id)
        docRef.set(newProduct).await()
    }


    suspend fun getProductsForAppointment(appointmentId: Long): List<Product> {
        val linkQuerySnapshot =
            collectionAppointmentProduct
                .whereEqualTo("appointmentId", appointmentId)
                .get()
                .await()

        val productList = mutableListOf<Product>()

        for (linkDoc in linkQuerySnapshot.documents) {
            val productIdFromLink = linkDoc.getLong("productId")

            if (productIdFromLink != null) {
                val productQuerySnapshot = collectionProduct
                    .whereEqualTo("id", productIdFromLink)
                    .limit(1)
                    .get()
                    .await()

                if (!productQuerySnapshot.isEmpty) {
                    val actualProductDoc = productQuerySnapshot.documents[0]
                    ProductConverter.fromSnapshot(actualProductDoc)?.let { product ->
                        productList.add(product)
                    }
                }
            }
        }
        return productList
    }

    suspend fun deleteProductLinkFromAppointment(appointmentId: Long, productId: Long): Boolean {
        val querySnapshot = collectionAppointmentProduct
            .whereEqualTo("appointmentId", appointmentId)
            .whereEqualTo("productId", productId)
            .limit(1)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val documentToDelete = querySnapshot.documents[0]
            documentToDelete.reference.delete().await()
            return true
        } else {
            return false
        }
    }
}
