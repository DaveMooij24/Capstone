package nl.hva.capstone.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.*
import nl.hva.capstone.repository.util.*

class SaleRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val saleCollection = firestore.collection("sale")
    private val saleProductCollection = firestore.collection("saleInformation")

    suspend fun getSales(): List<Sale> {
        val snapshot = saleCollection.get().await()
        return snapshot.documents.mapNotNull { SaleConverter.fromSnapshot(it) }
    }


    suspend fun addSale(sale: Sale) {
        saleCollection
            .document(sale.id.toString())
            .set(sale)
            .await()
    }

    suspend fun addSaleInformation(saleInformation: SaleInformation) {
        val docRef = saleProductCollection.document()
        val newSaleInformation = saleInformation.copy(id = docRef.id)
        docRef.set(newSaleInformation).await()
    }

    suspend fun addSaleInformation(saleInformationList: List<SaleInformation>) {
        val batch = Firebase.firestore.batch()

        saleInformationList.forEach { saleInformation ->
            val docRef = saleProductCollection.document()
            val newSaleInformation = saleInformation.copy(id = docRef.id)
            batch.set(docRef, newSaleInformation)
        }

        batch.commit().await()
    }

    suspend fun getSaleInformationForSale(saleId: Long): List<SaleInformation> {
        return try {
            val collectionLink = saleProductCollection
                .whereEqualTo("saleId", saleId)
                .get()
                .await()

            collectionLink.documents.mapNotNull { doc ->
                SaleInformationConverter.fromSnapshot(saleId, doc)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


}