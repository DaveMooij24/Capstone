package nl.hva.capstone.data.model

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class Sale(
    @SerializedName("id") val id: String,
    @SerializedName("dateTime") val dateTime: Timestamp,
)
