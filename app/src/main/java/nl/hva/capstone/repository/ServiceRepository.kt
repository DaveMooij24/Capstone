package nl.hva.capstone.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.Service
import nl.hva.capstone.repository.util.ServiceConverter

class ServiceRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val serviceCollection = firestore.collection("services")

    suspend fun saveService(service: Service) {
        serviceCollection
            .document(service.id.toString())
            .set(service)
            .await()
    }

    suspend fun getServices(): List<Service> {
        val snapshot = serviceCollection.get().await()

        return snapshot.documents.mapNotNull { doc ->
            ServiceConverter.fromSnapshot(doc)
        }
    }
}
