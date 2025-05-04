package nl.hva.capstone.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.Client
import nl.hva.capstone.repository.util.ClientConverter

class ClientRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val clientCollection = firestore.collection("clients")

    suspend fun saveClient(client: Client) {
        clientCollection
            .document(client.id.toString())
            .set(client)
            .await()
    }

    suspend fun getClientById(id: Long): Client? {
        val document = clientCollection.document(id.toString()).get().await()
        return ClientConverter.fromSnapshot(document)
    }

    suspend fun getClients(): List<Client> {
        val snapshot = clientCollection.get().await()

        return snapshot.documents.mapNotNull { doc ->
            ClientConverter.fromSnapshot(doc)
        }
    }
}
