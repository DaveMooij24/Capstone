package nl.hva.capstone.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.repository.util.ProductConverter
import java.util.*

class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val productCollection = firestore.collection("products")

    suspend fun saveProductWithImage(product: Product): Product {
        val imageUri = product.image
        val imageUrl = imageUri?.let { uploadImageToFirebase(it) }

        val productToSave = product.copy(image = imageUrl?.let { Uri.parse(it) })

        val dataMap = mapOf(
            "id" to productToSave.id,
            "name" to productToSave.name,
            "salePrice" to productToSave.salePrice,
            "purchasePrice" to productToSave.purchasePrice,
            "taxes" to productToSave.taxes,
            "image" to productToSave.image?.toString()
        )

        productCollection
            .document(productToSave.id.toString())
            .set(dataMap)
            .await()

        return productToSave
    }

    suspend fun saveProduct(product: Product) {
        val dataMap = mapOf(
            "id" to product.id,
            "name" to product.name,
            "salePrice" to product.salePrice,
            "purchasePrice" to product.purchasePrice,
            "taxes" to product.taxes,
            "image" to product.image?.toString()
        )

        productCollection
            .document(product.id.toString())
            .set(dataMap)
            .await()
    }

    suspend fun getProductById(id: Long): Product? {
        val document = productCollection.document(id.toString()).get().await()
        return ProductConverter.fromSnapshot(document)
    }

    suspend fun getProducts(): List<Product> {
        val snapshot = productCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            ProductConverter.fromSnapshot(doc)
        }
    }

    private suspend fun uploadImageToFirebase(uri: Uri): String {
        val fileName = "product_images/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
