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
    private val saleProductCollection = firestore.collection("saleProduct")
    private val saleServiceCollection = firestore.collection("saleService")

    private val productCollection = firestore.collection("products")
    private val serviceCollection = firestore.collection("services")

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

    suspend fun getSaleProductsForSale(saleId: Long): List<Product> {
        return try {

            val collectionLink =
                saleProductCollection
                    .whereEqualTo("saleId", saleId)
                    .get()
                    .await()

            val productList = mutableListOf<Product>()

            for (linkDoc in collectionLink.documents) {
                val productIdFromLink = linkDoc.getLong("productId")

                if (productIdFromLink != null) {
                    val productQuerySnapshot = productCollection
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
            productList
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSaleServicesForSale(saleId: Long): List<Service> {
        return try {

            val collectionLink =
                saleServiceCollection
                    .whereEqualTo("saleId", saleId)
                    .get()
                    .await()

            val serviceList = mutableListOf<Service>()

            for (linkDoc in collectionLink.documents) {
                val serviceIdFromLink = linkDoc.getLong("serviceId")

                if (serviceIdFromLink != null) {
                    val serviceQuerySnapshot = serviceCollection
                        .whereEqualTo("id", serviceIdFromLink)
                        .limit(1)
                        .get()
                        .await()

                    if (!serviceQuerySnapshot.isEmpty) {
                        val actualServiceDoc = serviceQuerySnapshot.documents[0]
                        ServiceConverter.fromSnapshot(actualServiceDoc)?.let { service ->
                            serviceList.add(service)
                        }
                    }
                }
            }
            serviceList
        } catch (e: Exception) {
            emptyList()
        }
    }

}