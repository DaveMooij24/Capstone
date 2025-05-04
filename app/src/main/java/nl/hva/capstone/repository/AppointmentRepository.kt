package nl.hva.capstone.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.Appointment
import nl.hva.capstone.repository.util.AppointmentConverter
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date

class AppointmentRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val appointmentCollection = firestore.collection("appointments")

    suspend fun saveAppointment(appointment: Appointment) {
        appointmentCollection
            .document(appointment.id.toString())
            .set(appointment)
            .await()
    }

    suspend fun getAppointmentById(id: Long): Appointment? {
        val document = appointmentCollection.document(id.toString()).get().await()
        return AppointmentConverter.fromSnapshot(document)
    }

    suspend fun deleteAppointment(id: Long) {
        appointmentCollection.document(id.toString()).delete().await()
    }

    suspend fun getAppointmentsBetween(startDate: Date, endDate: Date): List<Appointment> {
        val startTimestamp = Timestamp(startDate)
        val endTimestamp = Timestamp(endDate)

        val snapshot = appointmentCollection
            .whereGreaterThanOrEqualTo("dateTime", startTimestamp)
            .whereLessThanOrEqualTo("dateTime", endTimestamp)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            AppointmentConverter.fromSnapshot(doc)
        }
    }

    suspend fun getMostRecentAppointment(): Appointment? {
        val now = Timestamp.now()

        val snapshot = appointmentCollection
            .whereGreaterThanOrEqualTo("dateTime", now)
            .orderBy("dateTime", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .limit(1)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.let {
            AppointmentConverter.fromSnapshot(it)
        }
    }
}
