package nl.hva.capstone.data.model

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class Appointment(
    @SerializedName("id") val id: Long,
    @SerializedName("clientId") val clientId: Long,
    @SerializedName("serviceId") val serviceId: Long,
    @SerializedName("dateTime") val dateTime: Timestamp,
    @SerializedName("description") val description: String,
    @SerializedName("notes") val notes: String
)
