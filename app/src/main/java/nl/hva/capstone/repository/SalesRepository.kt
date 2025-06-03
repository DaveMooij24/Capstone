package nl.hva.capstone.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.*

class SaleRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val saleCollection = firestore.collection("sale")
    private val saleProductCollection = firestore.collection("saleProduct")
    private val saleServiceCollection = firestore.collection("saleService")

    suspend fun addSale(sale: Sale) {
        val docRef = saleCollection.document()
        val newSale = sale.copy(id = docRef.id)
        docRef.set(newSale).await()
    }

    suspend fun addSaleProduct(saleProduct: SaleProduct) {
        val docRef = saleProductCollection.document()
        val newSaleProduct = saleProduct.copy(id = docRef.id)
        docRef.set(newSaleProduct).await()
    }

    suspend fun addSaleProducts(saleProducts: List<SaleProduct>) {
        val batch = Firebase.firestore.batch()

        saleProducts.forEach { saleProduct ->
            val docRef = saleProductCollection.document()
            val newSaleProduct = saleProduct.copy(id = docRef.id)
            batch.set(docRef, newSaleProduct)
        }

        batch.commit().await()
    }

    suspend fun addSaleService(saleService: SaleService) {
        val docRef = saleProductCollection.document()
        val newSaleService = saleService.copy(id = docRef.id)
        docRef.set(newSaleService).await()
    }

    suspend fun addSaleServices(saleServices: List<SaleService>) {
        val batch = Firebase.firestore.batch()

        saleServices.forEach { saleService ->
            val docRef = saleProductCollection.document()
            val newSaleService = saleService.copy(id = docRef.id)
            batch.set(docRef, newSaleService)
        }

        batch.commit().await()
    }

    suspend fun getSaleProductsForSale(saleId: String): List<SaleProduct> {
        return try {
            saleProductCollection
                .whereEqualTo("saleId", saleId)
                .get()
                .await()
                .toObjects(SaleProduct::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSaleServicesForSale(saleId: String): List<SaleService> {
        return try {
            saleServiceCollection
                .whereEqualTo("saleId", saleId)
                .get()
                .await()
                .toObjects(SaleService::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

}