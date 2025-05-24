package nl.hva.capstone.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.hva.capstone.data.model.Appointment
import nl.hva.capstone.repository.util.AppointmentConverter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
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
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfTodayTimestamp = Timestamp(calendar.time)

        val snapshot = try {
            appointmentCollection
                .whereGreaterThanOrEqualTo("dateTime", startOfTodayTimestamp)
                .whereEqualTo("checkedOut", false)
                .orderBy("dateTime", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .await()
        } catch (e: Exception) {
            return null
        }

        return snapshot.documents.firstOrNull()?.let {
            AppointmentConverter.fromSnapshot(it)
        }
    }

    suspend fun updateAppointmentCheckoutStatus(appointmentId: Long, checkedOut: Boolean) {
        val querySnapshot = appointmentCollection
            .whereEqualTo("id", appointmentId)
            .limit(1)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val documentReference = querySnapshot.documents[0].reference
            documentReference.update("checkedOut", checkedOut).await()
        }
    }
}
